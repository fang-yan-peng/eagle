package eagle.jfaster.org.transport.support;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.transport.HeartBeat;
import eagle.jfaster.org.transport.HeartBeatFactory;

/**
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@SpiInfo(name = "noHeartBeat")
public class NoHeartBeatFactory implements HeartBeatFactory {

    @Override
    public Request createRequest() {
        throw new EagleFrameException("This method: eagle.jfaster.org.transport.support.NoHeartBeatFactory.createRequest not support by NoHeartBeatFactory");
    }

    @Override
    public HeartBeat createHeartBeat(MergeConfig config) {
        throw new EagleFrameException("This method: eagle.jfaster.org.transport.support.NoHeartBeatFactory.createHeartBeat not support by NoHeartBeatFactory");
    }

}
