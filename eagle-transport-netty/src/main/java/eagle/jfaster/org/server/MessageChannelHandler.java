package eagle.jfaster.org.server;

import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.support.EagleRequest;
import eagle.jfaster.org.rpc.support.EagleResponse;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
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
                    EagleResponse response= (EagleResponse) invokeRouter.routeAndInvoke(request);
                    response.setOpaque(request.getOpaque());
                    response.setNeedCompress(request.isNeedCompress());
                    ctx.writeAndFlush(response);
                }
            });
        } catch (RejectedExecutionException e) {
            EagleResponse response = new EagleResponse();
            response.setOpaque(request.getOpaque());
            response.setException(new EagleFrameException("process thread pool is full, reject %s", e.getMessage()));
            ctx.writeAndFlush(response);
            logger.info("process thread pool is full, reject, active={} poolSize={} corePoolSize={} maxPoolSize={} taskCount={} requestId={}",
                    threadExecutor.getActiveCount(), threadExecutor.getPoolSize(),
                    threadExecutor.getCorePoolSize(), threadExecutor.getMaximumPoolSize(),
                    threadExecutor.getTaskCount(), request.getOpaque());
        }
    }
}
