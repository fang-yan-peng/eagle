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
