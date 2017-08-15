package eagle.jfaster.org.transport.support;

import static eagle.jfaster.org.constant.EagleConstants.*;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.support.EagleRequest;
import eagle.jfaster.org.rpc.support.OpaqueGenerator;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.transport.HeartBeat;
import eagle.jfaster.org.transport.HeartBeatFactory;

/**
 *
 * 默认心跳检测工厂类
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@SpiInfo(name = "eagle")
public class EagleHeartBeatFactory implements HeartBeatFactory {

    @Override
    public Request createRequest() {
        EagleRequest request = new EagleRequest();
        request.setOpaque(OpaqueGenerator.getOpaque());
        request.setInterfaceName(HEARTBEAT_INTERFACE_NAME);
        request.setMethodName(HEARTBEAT_METHOD_NAME);
        return request;
    }

    @Override
    public HeartBeat createHeartBeat(MergeConfig config) {
        config.setInterfaceName(HEARTBEAT_INTERFACE_NAME);
        config.setVersion(DEFAULT_VERSION);
        return new EagleHeartBeat(config);
    }

}
