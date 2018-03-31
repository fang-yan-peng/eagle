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

package eagle.jfaster.org.protocol;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.pool.SuspendResumeLock;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.statistic.EagleStatsManager;
import eagle.jfaster.org.transport.Client;
import eagle.jfaster.org.util.ClockSource;
import eagle.jfaster.org.util.ExceptionUtil;
import eagle.jfaster.org.util.ReflectUtil;

/**
 *
 * 带统计信息的refer
 *
 * Created by fangyanpeng on 2017/8/22.
 */
public class StatsNettyRefer<T> extends NettyRefer<T> {

    private final String statsKey;

    public StatsNettyRefer(Client client, MergeConfig config, Class<T> type, SuspendResumeLock lock, InternalLogger log) {
        super(client, config, type, lock);
        statsKey = config.identity();
        EagleStatsManager.registerStatsItem(statsKey, log);
    }

    @Override
    public Object request(Request request) {
        try {
            if (lock.tryAcquire()) {
                long start = ClockSource.MILLINSTANCE.currentTime();
                try {
                    activeCnt.incrementAndGet();
                    return client.request(request);
                } finally {
                    lock.release();
                    EagleStatsManager.incInvoke(statsKey, ReflectUtil.getMethodDesc(request.getMethodName(), request.getParameterDesc()), ClockSource.MILLINSTANCE.elapsedMillis(start));
                }
            } else {
                String warn = String.format("'%s' too much request,more than actives:[%d]", config.identity(), lock.getMaxPermits());
                logger.warn(warn);
                throw new EagleFrameException(warn);
            }
        } catch (Throwable e) {
            throw ExceptionUtil.handleException(e);
        } finally {
            activeCnt.decrementAndGet();
        }
    }
}
