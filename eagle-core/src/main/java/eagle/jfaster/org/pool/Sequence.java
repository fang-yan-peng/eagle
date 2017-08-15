package eagle.jfaster.org.pool;

import eagle.jfaster.org.pool.number.LongAdder;
/**
 * Created by fangyanpeng1 on 2017/8/2.
 */
public interface Sequence
{
    /**
     * 当前序列加一
     */
    void increment();

    /**
     * 得到当前的sequence
     *
     */
    long get();

    /**
     * 根据java环境创建序列
     */
    final class Factory {

        public static Sequence create() {
            return new Java8Sequence();
        }

    }

    final class Java8Sequence extends LongAdder implements Sequence {
        @Override
        public long get() {
            return this.sum();
        }
    }
}

