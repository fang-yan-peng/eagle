package eagle.jfaster.org.transport;

import eagle.jfaster.org.rpc.RemoteInvoke;

/**
 * 服务端调用路由
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
public interface InvokeRouter <I,O> {
    O routeAndInvoke(I message);
    void addRemoteInvoke(RemoteInvoke invoke);
    boolean isExport();
}
