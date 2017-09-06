package eagle.jfaster.org.rpc.support;

import eagle.jfaster.org.rpc.ProtectStrategy;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.spi.SpiInfo;

/**
 *
 * 默认实现没有任何保护策略
 *
 * Created by fangyanpeng on 2017/9/5.
 */
@SpiInfo(name = "none")
public class NoneProtectStrategy implements ProtectStrategy {
    @Override
    public Response protect(Request request, RemoteInvoke invoker, int methodCnts) {
        return invoker.invoke(request);
    }
}
