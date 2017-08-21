package eagle.jfaster.org.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 *
 * 扩展fultureTask可以获取执行的任务
 *
 * Created by fangyanpeng on 2017/8/21.
 */
public class FutureTaskExt<V> extends FutureTask<V>{

    private final Runnable runnable;

    public FutureTaskExt(final Callable<V> callable) {
        super(callable);
        this.runnable = null;
    }

    public FutureTaskExt(final Runnable runnable, final V result) {
        super(runnable, result);
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
