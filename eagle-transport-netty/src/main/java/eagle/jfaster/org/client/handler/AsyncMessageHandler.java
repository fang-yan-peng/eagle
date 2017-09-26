/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

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
