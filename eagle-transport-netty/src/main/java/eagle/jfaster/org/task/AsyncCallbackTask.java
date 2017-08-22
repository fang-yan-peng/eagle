package eagle.jfaster.org.task;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.ResponseFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
@RequiredArgsConstructor
public class AsyncCallbackTask implements Runnable{

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(AsyncCallbackTask.class);

    @Getter
    private final ResponseFuture responseFuture;

    @Override
    public void run() {
        try {
            responseFuture.executeCallback();
        } catch (Throwable e) {
            logger.info("execute callback in executor exception, and callback throw", e);
        }
    }
}