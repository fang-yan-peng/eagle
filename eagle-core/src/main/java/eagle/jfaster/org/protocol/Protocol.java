package eagle.jfaster.org.protocol;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Exporter;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

/**
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@Spi(scope = Scope.SINGLETON)
public interface Protocol <T> {
    Exporter<T> createServer(RemoteInvoke<T> invoker);
    Refer<T> createRefer(MergeConfig config,Class<T> type);
    void close();
    boolean isOpen();
}
