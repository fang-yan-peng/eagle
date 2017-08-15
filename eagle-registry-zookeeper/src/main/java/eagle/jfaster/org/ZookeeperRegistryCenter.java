package eagle.jfaster.org;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * zk注册中心
 *
 * Created by fangyanpeng1 on 2016/12/28.
 */
@RequiredArgsConstructor
public class ZookeeperRegistryCenter implements CoordinatorRegistryCenter {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ZookeeperRegistryCenter.class);


    private final MergeConfig registryConfig;

    private final Map<String, PathChildrenCache> childrenCaches = new ConcurrentHashMap<>();

    private CuratorFramework client;

    private AtomicBoolean stat = new AtomicBoolean(false);

    @Override
    public void init() {
        if(!stat.compareAndSet(false,true)){
            return;
        }
        String address = registryConfig.getExt(ConfigEnum.address.getName(),ConfigEnum.address.getValue());
        logger.info("zookeeper registry center init, server lists is: {}.", address);
        String namespace = registryConfig.getExt(ConfigEnum.namespace.getName(),ConfigEnum.namespace.getValue());
        int baseSleepTimeMilliseconds = registryConfig.getExtInt(ConfigEnum.baseSleepTimeMilliseconds.getName(),ConfigEnum.baseSleepTimeMilliseconds.getIntValue());
        int maxSleepTimeMilliseconds = registryConfig.getExtInt(ConfigEnum.maxSleepTimeMilliseconds.getName(),ConfigEnum.maxSleepTimeMilliseconds.getIntValue());
        int maxRetries = registryConfig.getExtInt(ConfigEnum.maxRetries.getName(),ConfigEnum.maxRetries.getIntValue());
        int sessionTimeoutMilliseconds = registryConfig.getExtInt(ConfigEnum.sessionTimeoutMilliseconds.getName(),ConfigEnum.sessionTimeoutMilliseconds.getIntValue());
        int connectionTimeoutMilliseconds = registryConfig.getExtInt(ConfigEnum.connectionTimeoutMilliseconds.getName(),ConfigEnum.connectionTimeoutMilliseconds.getIntValue());
        String digest = registryConfig.getExt(ConfigEnum.digest.getName(),ConfigEnum.digest.getValue());
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTimeMilliseconds, maxRetries, maxSleepTimeMilliseconds))
                .namespace(namespace);
        if (0 != sessionTimeoutMilliseconds) {
            builder.sessionTimeoutMs(sessionTimeoutMilliseconds);
        }
        if (0 != connectionTimeoutMilliseconds) {
            builder.connectionTimeoutMs(connectionTimeoutMilliseconds);
        }
        if (!Strings.isNullOrEmpty(digest)) {
            builder.authorization("digest", digest.getBytes(Charsets.UTF_8))
                    .aclProvider(new ACLProvider() {

                        @Override
                        public List<ACL> getDefaultAcl() {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }

                        @Override
                        public List<ACL> getAclForPath(final String path) {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    });
        }
        client = builder.build();
        client.start();
        try {
            client.blockUntilConnected(maxSleepTimeMilliseconds * maxRetries, TimeUnit.MILLISECONDS);
            if (!client.getZookeeperClient().isConnected()) {
                client.close();
                throw new KeeperException.OperationTimeoutException();
            }
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public void close() {
        if(!stat.compareAndSet(true,false)){
            return;
        }
        for (Map.Entry<String, PathChildrenCache> each : childrenCaches.entrySet()) {
            try {
                each.getValue().close();
            } catch (IOException e) {
                logger.error("",e);
                throw new EagleFrameException(e);
            }
        }
        waitForCacheClose();
        CloseableUtils.closeQuietly(client);
    }

    /* TODO 等待500ms, cache先关闭再关闭client, 否则会抛异常
     * 因为异步处理, 可能会导致client先关闭而cache还未关闭结束.
     * 等待Curator新版本解决这个bug.
     * BUG地址：https://issues.apache.org/jira/browse/CURATOR-157
     */
    private void waitForCacheClose() {
        try {
            Thread.sleep(500L);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String get(final String key) {
        PathChildrenCache cache = findChildrenCach(key);
        if (null == cache) {
            return getDirectly(key);
        }
        ChildData resultInCache = cache.getCurrentData(key);
        if (null != resultInCache) {
            return null == resultInCache.getData() ? null : new String(resultInCache.getData(), Charsets.UTF_8);
        }
        return getDirectly(key);
    }

    private PathChildrenCache findChildrenCach(final String key) {
        for (Map.Entry<String, PathChildrenCache> entry : childrenCaches.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public String getDirectly(final String key) {
        try {
            return new String(client.getData().forPath(key), Charsets.UTF_8);
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public List<String> getChildrenKeys(final String key) {
        try {
            List<String> result = client.getChildren().forPath(key);
            Collections.sort(result, new Comparator<String>() {

                @Override
                public int compare(final String o1, final String o2) {
                    return o2.compareTo(o1);
                }
            });
            return result;
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            logger.error("ZookeeperRegistryCenter.getChildrenKeys fail ",ex);
            return Collections.emptyList();
        }
    }

    @Override
    public int getNumChildren(final String key) {
        try {
            Stat stat = client.getZookeeperClient().getZooKeeper().exists(getNameSpace() + key, false);
            if (null != stat) {
                return stat.getNumChildren();
            }
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
        return 0;
    }

    private String getNameSpace() {
        String namespace = registryConfig.getExt(ConfigEnum.namespace.getName(),ConfigEnum.namespace.getValue());
        return Strings.isNullOrEmpty(namespace) ? "" : "/" + namespace;
    }

    @Override
    public boolean isExisted(final String key) {
        try {
            return null != client.checkExists().forPath(key);
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public void persist(final String key, final String value) {
        try {
            if (!isExisted(key)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, value.getBytes(
                        Charsets.UTF_8));
            } else {
                update(key, value);
            }
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public void update(final String key, final String value) {
        try {
            client.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(Charsets.UTF_8)).and().commit();
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public void persistEphemeral(final String key, final String value) {
        try {
            if (isExisted(key)) {
                client.delete().deletingChildrenIfNeeded().forPath(key);
            }
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key, value.getBytes(Charsets.UTF_8));
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public String persistSequential(final String key, final String value) {
        try {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(key, value.getBytes(
                    Charsets.UTF_8));
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public void persistEphemeralSequential(final String key) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key);
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public void remove(final String key) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(key);
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
    }

    @Override
    public long getRegistryCenterTime(final String key) {
        long result = 0L;
        try {
            String path = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key);
            result = client.checkExists().forPath(path).getCtime();
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
        Preconditions.checkState(0L != result, "Cannot get registry center time.");
        return result;
    }

    @Override
    public Object getRawClient() {
        return client;
    }

    @Override
    public PathChildrenCache addChildrenCacheData(final String cachePath,boolean cacheData) {
        if(childrenCaches.containsKey(cachePath)){
            return childrenCaches.get(cachePath);
        }
        PathChildrenCache cache = new PathChildrenCache(client, cachePath,cacheData);
        try {
            cache.start();
            //CHECKSTYLE:OFF
        } catch (final Exception ex) {
            //CHECKSTYLE:ON
            throw new EagleFrameException(ex);
        }
        childrenCaches.put(cachePath, cache);
        return cache;
    }

    @Override
    public PathChildrenCache getRawChildrenCache(final String cachePath) {
        return childrenCaches.get(cachePath);
    }

}
