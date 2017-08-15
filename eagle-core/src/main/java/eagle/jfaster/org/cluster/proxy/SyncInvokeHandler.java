package eagle.jfaster.org.cluster.proxy;

import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.rpc.Request;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class SyncInvokeHandler<T> extends AbstractReferInvokeHandler<T> {

    public SyncInvokeHandler(List<ReferCluster<T>> referClusters, Class<T> clz) {
        super(referClusters, clz);
    }

    @Override
    protected Object handle(Method method,Request request) {
        return this.defaultCluster.call(request);
    }
}
