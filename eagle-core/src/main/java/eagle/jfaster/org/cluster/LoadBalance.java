package eagle.jfaster.org.cluster;

import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

import java.util.List;

/**
 *
 * 负载均衡策略
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface LoadBalance<T> {

    void refresh(List<Refer<T>> referers);

    Refer<T> select(Request request);
}
