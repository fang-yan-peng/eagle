package eagle.jfaster.org.client;

import eagle.jfaster.org.client.pool.NettySharedConnPool;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.transport.HeartBeatFactory;
import eagle.jfaster.org.util.RemotingUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;

/**
 *
 * 心跳检测，当读写都超时发送心跳包，如果连接不可用将连接标识为不可用
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
@RequiredArgsConstructor
public class NettyConnectionManager extends ChannelDuplexHandler {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyConnectionManager.class);


    private final MergeConfig config;

    private final NettySharedConnPool connPool;
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
                logger.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                HeartBeatFactory heartBeatFactory = SpiClassLoader.getClassLoader(HeartBeatFactory.class).getExtension(config.getExt(
                        ConfigEnum.heartbeatFactory.getName(),ConfigEnum.heartbeatFactory.getValue()));
                ctx.writeAndFlush(heartBeatFactory.createRequest());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
        logger.warn("NETTY CLIENT PIPELINE: exceptionCaught {}", remoteAddress);
        logger.warn("NETTY CLIENT PIPELINE: exceptionCaught exception.", cause);
        //连接池标识为连接不可用
        connPool.invalidateConnection(ctx.channel());

    }
}
