package eagle.jfaster.org;

import eagle.jfaster.org.registry.RegistryCenter;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.util.List;

/**
 * 注册中心.
 *
 * Created by fangyanpeng1 on 2016/12/28.
 */
public interface CoordinatorRegistryCenter extends RegistryCenter {

    /**
     * 直接从注册中心而非本地缓存获取数据.
     *
     * @param key 键
     * @return 值
     */
    String getDirectly(String key);

    /**
     * 获取子节点名称集合.
     *
     * @param key 键
     * @return 子节点名称集合
     */
    List<String> getChildrenKeys(String key);

    /**
     * 获取子节点数量.
     *
     * @param key 键
     * @return 子节点数量
     */
    int getNumChildren(String key);

    /**
     * 持久化临时注册数据.
     *
     * @param key 键
     * @param value 值
     */
    void persistEphemeral(String key, String value);

    /**
     * 持久化顺序注册数据.
     *
     * @param key 键
     * @return 包含10位顺序数字的znode名称
     */
    String persistSequential(String key, String value);

    /**
     * 持久化临时顺序注册数据.
     *
     * @param key 键
     */
    void persistEphemeralSequential(String key);

    /**
     * 添加本地缓存.
     *
     * @param cachePath 需加入缓存的路径
     * @Param cacheData 是否缓存节点数据
     */
    PathChildrenCache addChildrenCacheData(String cachePath,boolean cacheData);

    /**
     * 获取注册中心数据缓存对象.
     *
     * @param cachePath 缓存的节点路径
     * @return 注册中心数据缓存对象
     */
    PathChildrenCache getRawChildrenCache(String cachePath);

}
