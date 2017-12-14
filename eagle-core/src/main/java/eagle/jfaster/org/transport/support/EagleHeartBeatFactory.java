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

package eagle.jfaster.org.transport.support;

import static eagle.jfaster.org.constant.EagleConstants.*;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.support.EagleRequest;
import eagle.jfaster.org.rpc.support.OpaqueGenerator;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.transport.HeartBeat;
import eagle.jfaster.org.transport.HeartBeatFactory;

/**
 *
 * 默认心跳检测工厂类
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@SpiInfo(name = "eagle")
public class EagleHeartBeatFactory implements HeartBeatFactory {

    @Override
    public Request createRequest() {
        EagleRequest request = new EagleRequest();
        request.setOpaque(OpaqueGenerator.getDistributeOpaque());
        request.setInterfaceName(HEARTBEAT_INTERFACE_NAME);
        request.setMethodName(HEARTBEAT_METHOD_NAME);
        return request;
    }

    @Override
    public HeartBeat createHeartBeat(MergeConfig config) {
        config.setInterfaceName(HEARTBEAT_INTERFACE_NAME);
        config.setVersion(DEFAULT_VERSION);
        return new EagleHeartBeat(config);
    }

}
