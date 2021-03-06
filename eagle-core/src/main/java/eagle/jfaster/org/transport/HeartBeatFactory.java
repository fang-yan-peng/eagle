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

package eagle.jfaster.org.transport;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

/**
 * Created by fangyanpeng1 on 2017/7/31.
 */
@Spi(scope = Scope.SINGLETON)
public interface HeartBeatFactory {
    /**
     * 创建心跳包
     */
    Request createRequest();

    /**
     * 心跳响应
     */

    HeartBeat createHeartBeat(MergeConfig config);
}
