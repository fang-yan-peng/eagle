package eagle.jfaster.org.task;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.thread.FutureTaskExt;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.BlockingQueue;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
@RequiredArgsConstructor
public class AsyncCallbackMonitor implements Runnable{

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(AsyncCallbackMonitor.class);

    private final NettyClient client;

    @Override
    public void run() {
        MergeConfig config = client.getConfig();
        int callbackQueueSize = config.getExtInt(ConfigEnum.callbackQueueSize.getName(),ConfigEnum.callbackQueueSize.getIntValue());
        int warThreshold = callbackQueueSize*2/3 == 0 ? 1 : callbackQueueSize/3;
        int callbackWait = config.getExtInt(ConfigEnum.callbackWaitTime.getName(),ConfigEnum.callbackWaitTime.getIntValue());
        BlockingQueue<Runnable> callbackQueue = client.getCallbackQueue();
        try {
            if (callbackQueue.isEmpty()) {
                final FutureTaskExt runnable = (FutureTaskExt) callbackQueue.peek();
                if (null == runnable) {
                    client.setSuspend(false);
                    return;
                }
                if(callbackQueue.size() >= warThreshold) {
                    logger.info(String.format("%d task need to execute,maybe some callbacks execute too slow please check", callbackQueue.size()));
                }
                AsyncCallbackTask callbackTask = (AsyncCallbackTask) runnable.getRunnable();
                NettyResponseFuture responseFuture = (NettyResponseFuture) callbackTask.getResponseFuture();
                final long behind = System.currentTimeMillis() - responseFuture.getBeginTimestamp();
                // 如果队列过长，并且队头的任务等待时间过长，则将此client挂起。
                if (behind >= callbackWait && callbackQueue.size() >= warThreshold) {
                    logger.info(String.format("peek callback is not yet execute,has waited %ds and %d task need to execute,so stop accepting request",behind/1000, callbackQueue.size()));
                    client.setSuspend(true);
                }else {
                    client.setSuspend(false);
                }
            }else {
                client.setSuspend(false);
            }
        } catch (Throwable e) {
            logger.error("AsyncCallbackMonitor:",e);
        }
    }
}
