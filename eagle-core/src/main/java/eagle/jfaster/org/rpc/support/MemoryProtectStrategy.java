package eagle.jfaster.org.rpc.support;

import eagle.jfaster.org.rpc.ProtectStrategy;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.statistic.EagleStatsManager;
import eagle.jfaster.org.util.RequestUtil;

import static eagle.jfaster.org.util.RequestUtil.buildRejectResponse;

/**
 *
 * 根据系统内存使用情况对服务端进行保护
 *
 * Created by fangyanpeng on 2017/9/6.
 */
@SpiInfo(name = "memory")
public class MemoryProtectStrategy implements ProtectStrategy {
    @Override
    public Response protect(Request request, RemoteInvoke invoker, int methodCnt) {
        if(EagleStatsManager.getMemoryUsedPct() > 90.0){
            return buildRejectResponse(String.format("Not allow invoke service %s because of memory usages of the server is over 90%", RequestUtil.getRequestDesc(request)));
        }
        return invoker.invoke(request);
    }
}
