package eagle.jfaster.org.transport;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

/**
 * Created by fangyanpeng1 on 2017/7/31.
 */
@Spi(scope = Scope.SINGLETON)
public interface HeartBeatFactory {
    /**
     * 创建心跳包
     */
    Request createRequest();

    /**
     * 心跳响应
     */

    HeartBeat createHeartBeat(MergeConfig config);
}
