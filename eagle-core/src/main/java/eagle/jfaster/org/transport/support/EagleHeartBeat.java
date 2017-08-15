package eagle.jfaster.org.transport.support;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.transport.HeartBeat;
import lombok.RequiredArgsConstructor;

/**
 * 心跳响应默认实现
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@RequiredArgsConstructor
public class EagleHeartBeat implements HeartBeat {

    private final MergeConfig config;

    @Override
    public String heartBeat() {
        return "success";
    }

    @Override
    public MergeConfig getConfig() {
        return config;
    }
}
