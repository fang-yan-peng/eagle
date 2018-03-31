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

package eagle.jfaster.org.task;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.pool.NettySharedConnPool;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.transport.HeartBeatFactory;
import lombok.RequiredArgsConstructor;

/**
 * Created by fangyanpeng on 2017/8/27.
 */
@RequiredArgsConstructor
public class HeartBeatTask implements Runnable {

    private final MergeConfig config;

    private final NettySharedConnPool connPool;

    private final NettyClient client;

    @Override
    public void run() {
        try {
            HeartBeatFactory heartBeatFactory = SpiClassLoader.getClassLoader(HeartBeatFactory.class).getExtension(config.getExt(ConfigEnum.heartbeatFactory.getName(), ConfigEnum.heartbeatFactory.getValue()));
            if (!client.getStat().get() && connPool.getTotalConnections() == 0) {
                client.request(heartBeatFactory.createRequest());
            }
        } catch (Throwable e) {

        }
    }
}
