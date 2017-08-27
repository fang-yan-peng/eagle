package eagle.jfaster.org.task;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.pool.NettySharedConnPool;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.transport.HeartBeatFactory;
import lombok.RequiredArgsConstructor;

/**
 * Created by fangyanpeng on 2017/8/27.
 */
@RequiredArgsConstructor
public class HeartBeatTask implements Runnable {

    private final MergeConfig config;

    private final NettySharedConnPool connPool;

    private final NettyClient client;

    @Override
    public void run() {
        try {
            HeartBeatFactory heartBeatFactory = SpiClassLoader.getClassLoader(HeartBeatFactory.class).getExtension(config.getExt(ConfigEnum.heartbeatFactory.getName(),ConfigEnum.heartbeatFactory.getValue()));
            if(!client.getStat().get() && connPool.getTotalConnections() == 0){
                client.request(heartBeatFactory.createRequest());
            }
        } catch (Throwable e) {

        }
    }
}
