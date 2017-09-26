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

package eagle.jfaster.org.rpc.support;

import eagle.jfaster.org.rpc.ProtectStrategy;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.statistic.EagleStatsManager;
import eagle.jfaster.org.util.RequestUtil;

import static eagle.jfaster.org.util.RequestUtil.buildRejectResponse;

/**
 *
 * 根据系统内存使用情况对服务端进行保护
 *
 * Created by fangyanpeng on 2017/9/6.
 */
@SpiInfo(name = "memory")
public class MemoryProtectStrategy implements ProtectStrategy {
    @Override
    public Response protect(Request request, RemoteInvoke invoker, int methodCnt) {
        if(EagleStatsManager.getMemoryUsedPct() > 90.0){
            return buildRejectResponse(String.format("Not allow invoke service %s because of memory usages of the server is over 90%", RequestUtil.getRequestDesc(request)));
        }
        return invoker.invoke(request);
    }
}
