package eagle.jfaster.org.protocol;

import eagle.jfaster.org.rpc.Exporter;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.transport.Server;
import lombok.RequiredArgsConstructor;

/**
 * Created by fangyanpeng1 on 2017/7/31.
 */
@RequiredArgsConstructor
public class NettyRpcExporter<T> implements Exporter<T> {

    private final RemoteInvoke<T> invoker;

    private final Server server;

    @Override
    public RemoteInvoke<T> getInvoker() {
        return invoker;
    }

    @Override
    public void init() {

    }

    @Override
    public void close() {
        server.shutdown();
    }
}
