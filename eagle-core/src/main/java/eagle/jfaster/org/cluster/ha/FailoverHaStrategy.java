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


package eagle.jfaster.org.cluster.ha;

import eagle.jfaster.org.cluster.LoadBalance;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "failover")
public class FailoverHaStrategy<T> extends AbstractHaStrategy<T> {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(FailoverHaStrategy.class);

    @Override
    public Object call(Request request, LoadBalance<T> loadBalance) {
        int retry = config.getExtInt(ConfigEnum.retries.getName(), ConfigEnum.retries.getIntValue());
        retry = retry < 0 ? 1 : retry;
        for (int i = 0; i <= retry; ++i) {
            Refer<T> refer = loadBalance.select(request);
            try {
                return refer.request(request);
            } catch (Throwable e) {
                if (i > retry) {
                    throw e;
                }
                logger.warn(String.format("Failover call fail for interface: '%s',cause: '%s'", request.getInterfaceName(), e.getMessage()));
            }
        }
        throw new EagleFrameException("Failover call can'nt run here!");
    }
}
