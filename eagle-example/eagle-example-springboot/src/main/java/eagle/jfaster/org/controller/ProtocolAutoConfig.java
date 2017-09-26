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

package eagle.jfaster.org.controller;
import eagle.jfaster.org.config.ProtocolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class ProtocolAutoConfig {

    @Bean(name = "protoConf")
    public ProtocolConfig getProtocolConfig(
            @Value("${protocol.name}") String eagle,
            @Value("${protocol.serialization}") String serialization,
            @Value("${protocol.use-default}") boolean useDefault,
            @Value("${protocol.max-content-length}") int maxContentLength,
            @Value("${protocol.max-server-connection}") int maxServerConnection,
            @Value("${protocol.core-worker-thread}") int coreWorkerThread,
            @Value("${protocol.max-worker-thread}") int maxWorkerThread,
            @Value("${protocol.worker-queue-size}") int workerQueueSize){
        ProtocolConfig protoConfig = new ProtocolConfig();
        protoConfig.setId("protoConf");
        protoConfig.setName(eagle);
        protoConfig.setSerialization(serialization);
        protoConfig.setUseDefault(useDefault);
        protoConfig.setMaxContentLength(maxContentLength);
        protoConfig.setMaxServerConnection(maxServerConnection);
        protoConfig.setCoreWorkerThread(coreWorkerThread);
        protoConfig.setMaxWorkerThread(maxWorkerThread);
        protoConfig.setWorkerQueueSize(workerQueueSize);
        return protoConfig;
    }
}
