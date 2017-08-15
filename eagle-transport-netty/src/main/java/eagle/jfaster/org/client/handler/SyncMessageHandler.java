package eagle.jfaster.org.client.handler;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.rpc.Response;

/**
 * 同步处理response
 * 
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class SyncMessageHandler extends AbstractMessageChannelHandler {

    public SyncMessageHandler(NettyClient client) {
        super(client);
    }

    @Override
    protected void handle(Response response, NettyResponseFuture future) {
        if(response.getException() != null){
            future.onFail(response.getException());
        }else {
            future.onSuccess(response.getValue());
        }
    }
}
