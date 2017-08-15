package eagle.jfaster.org.listener;

import com.google.common.base.Strings;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * Created by fangyanpeng1 on 2017/8/6.
 */
@RequiredArgsConstructor
public class RefListener extends AbstractChildrenDataListener {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(RefListener.class);


    //zk地址配置
    private final MergeConfig registryConfig;

    //
    private final String refHost;

    //节点变化通知
    private final ServiceChangeListener changeListener;

    @Override
    protected void dataChanged(String path, PathChildrenCacheEvent.Type eventType, String data) {
        if(eventType == PathChildrenCacheEvent.Type.CHILD_UPDATED){
            try {
                String host = PathUtil.getHostByPath(path);
                if(Strings.isNullOrEmpty(host) || !host.equals(refHost)){
                    return;
                }
                this.changeListener.refChange(registryConfig, MergeConfig.decode(data));
            } catch (Exception e) {
                logger.error("Zookeeper service listener failed ",e);
            }
        }
    }
}
