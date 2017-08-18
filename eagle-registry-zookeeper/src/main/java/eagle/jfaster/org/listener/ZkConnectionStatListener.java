package eagle.jfaster.org.listener;

import eagle.jfaster.org.CoordinatorRegistryCenter;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * zk连接监听器
 *
 * Created by fangyanpeng on 2017/8/18.
 */
@RequiredArgsConstructor
public class ZkConnectionStatListener implements ConnectionStateListener {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZkConnectionStatListener.class);

    //zk地址配置
    private final MergeConfig registryConfig;

    //节点变化通知
    private final ServiceChangeListener changeListener;

    //注册中心
    private final CoordinatorRegistryCenter registryCenter;

    //refer监听地址
    private final String servicePath;

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState state) {
        if(ConnectionState.SUSPENDED == state || ConnectionState.LOST == state){
            logger.info(String.format("%s has lost connection from zookeeper", servicePath));
        }else if(ConnectionState.RECONNECTED == state){
            try {
                PathUtil.rebalance(registryCenter,registryConfig,changeListener,servicePath);
            } catch (Exception e) {
                logger.error(String.format("%s connection reconnected to rebalance error",servicePath),e);
            }
        }
    }
}
