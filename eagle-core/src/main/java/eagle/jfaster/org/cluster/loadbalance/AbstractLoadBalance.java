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

import eagle.jfaster.org.cluster.LoadBalance;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;

import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
public abstract class AbstractLoadBalance<T> implements LoadBalance<T> {

    public static final int MAX_REFER_COUNT = 10;

    protected List<Refer<T>> refers;

    @Override
    public void refresh(List<Refer<T>> refers) {
        this.refers = refers;
    }

    @Override
    public Refer<T> select(Request request) {
        List<Refer<T>> refers = this.refers;
        if (refers == null) {
            throw new EagleFrameException("No alive refers to request,interfaceName:%s", request.getInterfaceName());
        }
        Refer<T> refer = null;
        if (refers.size() > 1) {
            refer = doSelect(request);
        } else if (refers.size() == 1 && refers.get(0).isAlive()) {
            refer = refers.get(0);
        }
        if (refer != null) {
            return refer;
        }
        throw new EagleFrameException("No alive refers to request,interfaceName:%s", request.getInterfaceName());
    }

    public abstract Refer<T> doSelect(Request request);


}
