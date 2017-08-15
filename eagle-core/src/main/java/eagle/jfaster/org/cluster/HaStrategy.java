package eagle.jfaster.org.cluster;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

/**
 *
 * ha策略
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface HaStrategy<T> {

    void setConfig(MergeConfig config);

    Object call(Request request, LoadBalance<T> loadBalance);
}
