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

