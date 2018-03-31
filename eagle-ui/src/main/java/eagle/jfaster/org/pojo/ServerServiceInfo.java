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

package eagle.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng on 2017/8/25.
 */
public class ServerServiceInfo implements ServiceCommonSetter {

    @Setter
    @Getter
    String protocol;

    @Setter
    @Getter
    String serialization;

    @Setter
    @Getter
    String group;

    @Setter
    @Getter
    String serviceName;

    @Setter
    @Getter
    String host;

    @Setter
    @Getter
    Integer port;

    @Setter
    @Getter
    String codec;

    @Setter
    @Getter
    Integer maxContentLength;

    @Setter
    @Getter
    Boolean useNative;

    @Setter
    @Getter
    String heartbeatFactory;

    @Setter
    @Getter
    String version;

    @Setter
    @Getter
    Integer weight;

    @Setter
    @Getter
    Integer selectThreadSize;

    @Setter
    @Getter
    Integer maxServerConnection;

    @Setter
    @Getter
    Integer coreWorkerThread;

    @Setter
    @Getter
    Integer maxWorkerThread;

    @Setter
    @Getter
    Integer workerQueueSize;

    @Setter
    @Getter
    String protectStrategy;

    @Override
    public void setProcess(Integer process) {

    }

    @Override
    public Integer getProcess() {
        return null;
    }
}
