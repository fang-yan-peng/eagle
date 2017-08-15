package eagle.jfaster.org.client.channel;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import io.netty.channel.Channel;

/**
 * 异步处理channel
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class AsyncNettyChannel extends AbstractNettyChannel {

    public AsyncNettyChannel(NettyClient client, Channel channel) {
        super(client, channel);
    }

    @Override
    protected Object handle(long timeout,NettyResponseFuture responseFuture) {
        return null;
    }
}
