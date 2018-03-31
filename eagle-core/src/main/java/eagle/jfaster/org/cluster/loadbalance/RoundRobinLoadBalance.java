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

package eagle.jfaster.org.cluster.loadbalance;

import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询访问refer
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "roundrobin")
public class RoundRobinLoadBalance<T> extends AbstractLoadBalance<T> {

    private AtomicInteger idx = new AtomicInteger(0);

    @Override
    public Refer<T> doSelect(Request request) {
        List<Refer<T>> refers = this.refers;
        int index = getNextPositive();
        for (int i = 0; i < refers.size(); i++) {
            Refer<T> ref = refers.get((i + index) % refers.size());
            if (ref.isAlive()) {
                return ref;
            }
        }
        return null;
    }

    // get positive int
    private int getNextPositive() {
        return 0x7fffffff & idx.incrementAndGet();
    }
}

