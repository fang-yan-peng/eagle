package eagle.jfaster.org.cluster;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;
import java.util.List;

/**
 * refer集群，同一个服务暴露多个接口或ip
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface ReferCluster<T> {

    void init();

    void setConfig(MergeConfig config);

    void setLoadBalance(LoadBalance<T> loadBalance);

    void setHaStrategy(HaStrategy<T> haStrategy);

    void refresh(List<Refer<T>> referers);

    List<Refer<T>> getRefers();

    LoadBalance<T> getLoadBalance();

    Class<T> getInterface();

    Object call(Request request);

    void destroy();

    boolean isAvailable();

    MergeConfig getConfig();
}
