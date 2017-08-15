package eagle.jfaster.org.pool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 并发控制
 *
 * Created by fangyanpeng1 on 2017/8/3.
 */
public class SuspendResumeLock {

    public static final SuspendResumeLock FAUX_LOCK = new SuspendResumeLock(false,0,0) {
        @Override
        public void acquire() {}

        @Override
        public void release() {}

        @Override
        public void suspend() {}

        @Override
        public void resume() {}

        @Override
        public boolean tryAcquire() throws InterruptedException {
            return true;
        }
    };

    @Setter
    @Getter
    private int maxPermits = 10000;

    private long waitMs = 3000;

    private final Semaphore acquisitionSemaphore;


    public SuspendResumeLock(int maxPermits,long waitMs) {
        this(true,maxPermits,waitMs);
    }

    private SuspendResumeLock(final boolean createSemaphore,int maxPermits,long waitMs) {
        this.maxPermits = maxPermits;
        this.waitMs = waitMs;
        acquisitionSemaphore = (createSemaphore ? new Semaphore(maxPermits, true) : null);
    }

    public void acquire() {
        acquisitionSemaphore.acquireUninterruptibly();
    }

    public void release() {
        acquisitionSemaphore.release();
    }

    public void suspend() {
        acquisitionSemaphore.acquireUninterruptibly(maxPermits);
    }

    public void resume() {
        acquisitionSemaphore.release(maxPermits);
    }

    public boolean tryAcquire() throws InterruptedException {
        return acquisitionSemaphore.tryAcquire(waitMs,TimeUnit.MILLISECONDS);
    }
}
