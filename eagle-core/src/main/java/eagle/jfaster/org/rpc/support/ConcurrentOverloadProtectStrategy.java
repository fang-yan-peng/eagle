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

import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.rpc.ProtectStrategy;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.util.RequestUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static eagle.jfaster.org.util.RequestUtil.buildRejectResponse;

/**
 *
 * 并发过载保护策略
 *
 * Created by fangyanpeng on 2017/9/5.
 */
@SpiInfo(name = "concurrent")
public class ConcurrentOverloadProtectStrategy implements ProtectStrategy {

    private ConcurrentMap<String, AtomicInteger> requestCounters = new ConcurrentHashMap<>();

    private AtomicInteger totalCounter = new AtomicInteger(0);

    @Override
    public Response protect(Request request, RemoteInvoke invoker, int methodCnt) {
        int maxWorkerThread = invoker.getConfig().getExtInt(ConfigEnum.maxWorkerThread.getName(),ConfigEnum.maxWorkerThread.getIntValue());
        String reqKey = RequestUtil.getRequestDesc(request);
        try {
            int totalReqCnt = totalCounter.incrementAndGet();
            int methodReqCnt = incrRequestCounter(reqKey);
            //如果只有一个方法暴露则不进行保护，如果并发过多，业务处理不过来，提交线程池任务根据拒绝策略处理
            if(methodCnt == 1){
                return invoker.invoke(request);
            }
            //如果该方法请求大于最大线程的1／2，并且所有方法的总请求量大于线程的 3／4则进行保护
            if(methodReqCnt > (maxWorkerThread / 2) && totalReqCnt > (maxWorkerThread * 3 / 4)){
                return buildRejectResponse(String.format("Not allow invoke service '%s' because of too many invoke at the same time",reqKey));
            }
            //如果总量达到量3／4，并且暴露的方法较多，则进行保护
            if(methodCnt >= 4 && totalReqCnt > (maxWorkerThread * 3 / 4) && methodReqCnt > (maxWorkerThread * 1 / 4)){
                return buildRejectResponse(String.format("Not allow invoke service '%s' because of too many invoke at the same time",reqKey));
            }
            return invoker.invoke(request);
        } finally {
            totalCounter.decrementAndGet();
            decrRequestCounter(reqKey);
        }
    }

    private int incrRequestCounter(String requestKey) {
        AtomicInteger counter = requestCounters.get(requestKey);
        if (counter == null) {
            counter = new AtomicInteger(0);
            requestCounters.putIfAbsent(requestKey, counter);
            counter = requestCounters.get(requestKey);
        }
        return counter.incrementAndGet();
    }

    private int decrRequestCounter(String requestKey) {
        AtomicInteger counter = requestCounters.get(requestKey);
        if (counter == null) {
            return 0;
        }
        return counter.decrementAndGet();
    }

}
