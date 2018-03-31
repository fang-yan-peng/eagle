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

import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;

/**
 * 用于共享锁的序列
 *
 * Created by fangyanpeng1 on 2017/8/2.
 */
public final class QueuedSequenceSynchronizer {
    private final Sequence sequence;
    private final Synchronizer synchronizer;

    public QueuedSequenceSynchronizer() {
        this.synchronizer = new Synchronizer();
        this.sequence = Sequence.Factory.create();
    }

    /**
     * 通知所有等待的线程
     */
    public void signal() {
        synchronizer.releaseShared(1);
    }

    /**
     * 获取当前的序列数
     */
    public long currentSequence() {
        return sequence.get();
    }

    /**
     * 阻塞当前线程，直到序列增加的特定值，或者等待超时
     *
     */
    public boolean waitUntilSequenceExceeded(long sequence, long nanosTimeout) throws InterruptedException {
        return synchronizer.tryAcquireSharedNanos(sequence, nanosTimeout);
    }

    /**
     * 检测是否有线程在等待
     *
     */
    public boolean hasQueuedThreads() {
        return synchronizer.hasQueuedThreads();
    }

    /**
     * 获取同步队列里的线程数
     */
    public int getQueueLength() {
        return synchronizer.getQueueLength();
    }

    private final class Synchronizer extends AbstractQueuedLongSynchronizer {
        private static final long serialVersionUID = 104753538004341218L;

        @Override
        protected long tryAcquireShared(final long seq) {
            return sequence.get() - (seq + 1);
        }

        @Override
        protected boolean tryReleaseShared(final long unused) {
            sequence.increment();
            return true;
        }
    }
}
