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

package eagle.jfaster.org.server;

import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.support.EagleRequest;
import eagle.jfaster.org.rpc.support.EagleResponse;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.rpc.support.TraceContext;
import eagle.jfaster.org.transport.InvokeRouter;
import eagle.jfaster.org.transport.StandardThreadExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import java.util.concurrent.RejectedExecutionException;

/**
 * 请求处理类，为了防止业务逻辑阻塞io线程，所以采用线程池处理业务逻辑
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@RequiredArgsConstructor
public class MessageChannelHandler extends SimpleChannelInboundHandler<EagleRequest> {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(MessageChannelHandler.class);


    private final InvokeRouter<Request,Response> invokeRouter;

    private final StandardThreadExecutor threadExecutor;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final EagleRequest request) throws Exception {
        // 使用线程池方式处理
        try {
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    TraceContext.setOpaque(request.getOpaque());
                    EagleResponse response= (EagleResponse) invokeRouter.routeAndInvoke(request);
                    TraceContext.clear();
                    response.setOpaque(request.getOpaque());
                    response.setNeedCompress(request.isNeedCompress());
                    ctx.writeAndFlush(response);
                }
            });
        } catch (RejectedExecutionException e) {
            EagleResponse response = new EagleResponse();
            response.setOpaque(request.getOpaque());
            response.setException(new EagleFrameException("process thread pool is full, reject '%s'", e.getMessage()));
            ctx.writeAndFlush(response);
            logger.info(String.format("process thread pool is full, reject, active='%d' poolSize='%d' corePoolSize='%d' maxPoolSize='%d' taskCount='%d'",
                    threadExecutor.getActiveCount(), threadExecutor.getPoolSize(),
                    threadExecutor.getCorePoolSize(), threadExecutor.getMaximumPoolSize(), threadExecutor.getTaskCount()));
        }
    }
}
