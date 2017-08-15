package eagle.jfaster.org.client.handler;
import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.rpc.Response;

/**
 * 异步处理response
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class AsyncMessageHandler extends AbstractMessageChannelHandler {

    public AsyncMessageHandler(NettyClient client) {
        super(client);
    }

    @Override
    protected void handle(Response response,NettyResponseFuture future) {
        if(response.getException() != null){
            future.setException(response.getException());
        }else {
            future.setValue(response.getValue());
        }
        client.executeInvokeCallback(future);
    }
}
