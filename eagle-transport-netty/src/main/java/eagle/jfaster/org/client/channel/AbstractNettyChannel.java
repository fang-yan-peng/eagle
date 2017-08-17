package eagle.jfaster.org.client.channel;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.client.pool.NettyPoolEntry;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.MethodInvokeCallBack;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketAddress;

/**
 * netty channel 包装类
 *
 * Created by fangyanpeng1 on 2017/8/3.
 */
public abstract class AbstractNettyChannel {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNettyChannel.class);

    protected NettyClient client;

    @Getter
    private Channel channel;

    private MergeConfig config;

    private MethodInvokeCallBack callBack;

    private boolean sync = true;

    @Getter
    @Setter
    private NettyPoolEntry poolEntry;

    public AbstractNettyChannel(NettyClient client, Channel channel) {
        this.client = client;
        this.channel = channel;
        this.config = client.getConfig();
        this.callBack = client.getCallBack();
        sync = (callBack == null);
    }

    public Object request(Request request) throws Exception {
        int timeout = config.getExtInt(ConfigEnum.requestTimeout.getName(),ConfigEnum.requestTimeout.getIntValue());
        if(timeout < 0){
            throw new EagleFrameException("The request timeout of %s is not allowed to set 0",timeout);
        }
        try {
            final int opaque = request.getOpaque();
            final NettyResponseFuture responseFuture = new NettyResponseFuture(opaque,timeout,callBack);
            client.addCallBack(opaque,responseFuture);
            final SocketAddress addr = channel.remoteAddress();
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        client.resetErrorCount();
                        return;
                    } else {
                        client.incrErrorCount();
                        responseFuture.setSendRequestOK(false);
                    }

                    client.removeCallBack(opaque);
                    Exception e = new EagleFrameException(f.cause());
                    if (!sync){
                        responseFuture.setException(e);
                        client.executeInvokeCallback(responseFuture);
                    }else {
                        responseFuture.onFail(e);
                    }
                    logger.warn("send a request command to channel <" + addr + "> failed.");
                }
            });
            return handle(timeout,responseFuture);
        } catch (Exception e) {
            logger.error("send a request to channel failed",e);
            throw e;
        }

    }

    protected abstract Object handle(long timeout,NettyResponseFuture responseFuture) throws Exception;

    public void close(){
        try {
            RemotingUtil.closeChannel(channel,"AbstractNettyChannel close");
        } catch (Exception e) {
            logger.info("Close channel error ",e);
        }
    }
}
