package eagle.jfaster.org.client.handler;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.rpc.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

/**
 *
 * 响应处理器
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
@RequiredArgsConstructor
public abstract class AbstractMessageChannelHandler extends SimpleChannelInboundHandler<Response> {

    protected final NettyClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        int opaque = response.getOpaque();
        NettyResponseFuture future = client.removeCallBack(opaque);
        if(future != null){
            handle(response,future);
        }

    }

    protected abstract void handle(Response response,NettyResponseFuture future);
}
