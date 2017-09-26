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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

    private final NettyClient client;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
                logger.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                HeartBeatFactory heartBeatFactory = SpiClassLoader.getClassLoader(HeartBeatFactory.class).getExtension(config.getExt(ConfigEnum.heartbeatFactory.getName(),ConfigEnum.heartbeatFactory.getValue()));
                ctx.writeAndFlush(heartBeatFactory.createRequest()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isSuccess()){
                            client.resetErrorCount();
                        }
                    }
                });
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
