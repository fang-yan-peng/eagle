package eagle.jfaster.org.rpc;

import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

/**
 *
 * 服务端过载保护策略
 *
 * Created by fangyanpeng on 2017/9/5.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface ProtectStrategy {
    Response protect(Request request,RemoteInvoke invoker,int methodCnts);
}
