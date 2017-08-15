package eagle.jfaster.org.rpc;

import eagle.jfaster.org.config.common.MergeConfig;

/**
 * Created by fangyanpeng1 on 2017/7/31.
 */
public interface Refer <T> {
    MergeConfig getConfig();
    int getActiveCount();
    Class<T> getType();
    void updateConfig(MergeConfig refConfig);
    void close();
    void init();
    Object request(Request request);
    boolean isAlive();
}
