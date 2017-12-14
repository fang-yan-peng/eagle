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
            throw new EagleFrameException("Request timeout,timeout: [%d]",timeout);
        }
    }
}
