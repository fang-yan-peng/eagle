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
import java.util.concurrent.ThreadLocalRandom;

/**
 * 低并发refer优先访问
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "activeWeigth")
public class ActiveWeigthLoadBalance<T> extends AbstractLoadBalance<T> {

    @Override
    public Refer<T> doSelect(Request request) {
        List<Refer<T>> refers = this.refers;
        int referSize = refers.size();
        int startIndex = ThreadLocalRandom.current().nextInt(referSize);
        int currentCursor = 0;
        int currentAvailableCursor = 0;
        Refer<T> refer = null;
        while (currentAvailableCursor < MAX_REFER_COUNT && currentCursor < referSize) {
            Refer<T> temp = refers.get((startIndex + currentCursor) % referSize);
            currentCursor++;

            if (!temp.isAlive()) {
                continue;
            }
            currentAvailableCursor++;
            if (refer == null) {
                refer = temp;
            } else {
                if (compare(refer, temp) > 0) {
                    refer = temp;
                }
            }
        }

        return refer;
    }

    private int compare(Refer<T> refer1, Refer<T> refer2) {
        return refer1.getActiveCount() - refer2.getActiveCount();
    }

}
