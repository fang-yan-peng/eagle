package eagle.jfaster.org.cluster.ha;

import eagle.jfaster.org.cluster.LoadBalance;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;

/**
 * 根据负载策略选取一个refer，调用失败不重试
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "failfast")
public class FailfastHaStrategy<T> extends AbstractHaStrategy<T> {

    @Override
    public Object call(Request request, LoadBalance<T> loadBalance) {
        Refer<T> refer = loadBalance.select(request);
        return refer.request(request);
    }
}
