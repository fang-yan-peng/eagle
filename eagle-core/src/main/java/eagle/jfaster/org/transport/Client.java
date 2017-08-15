package eagle.jfaster.org.transport;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;

/**
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
public interface Client {
    MergeConfig getConfig();
    boolean isAlive();
    void start();
    void shutdown();
    Object request(Request request);
}
