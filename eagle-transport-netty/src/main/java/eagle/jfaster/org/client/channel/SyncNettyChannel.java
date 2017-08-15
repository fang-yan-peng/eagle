package eagle.jfaster.org.client.channel;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.exception.EagleFrameException;
import io.netty.channel.Channel;

/**
 * 同步处理channel
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class SyncNettyChannel extends AbstractNettyChannel {

    public SyncNettyChannel(NettyClient client, Channel channel) {
        super(client, channel);
    }

    @Override
    protected Object handle(long timeout,NettyResponseFuture responseFuture) throws Exception {
        Object ret = null;
        try {
            ret = responseFuture.getValue(timeout + 100);
        } finally {
            client.removeCallBack(responseFuture.getOpaque());
        }
        if(ret != null){
            return ret;
        }
        if(responseFuture.isSendRequestOK() && !responseFuture.isTimeout()){
            return ret;
        }else {
            throw new EagleFrameException("Request timeout,timeout: %d",timeout);
        }
    }
}
