package eagle.jfaster.org.transport;

import eagle.jfaster.org.config.common.MergeConfig;

/**
 * 心跳检测
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
public interface HeartBeat {
    String heartBeat();
    MergeConfig getConfig();
}
