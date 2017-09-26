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

import eagle.jfaster.org.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class RegistryAutoConfig {

    @Bean(name = "regConf")
    public RegistryConfig getRegistryConfig(
            @Value("${registry.protocol}") String protocol,
            @Value("${registry.address}") String address,
            @Value("${registry.namespace}") String namespace,
            @Value("${registry.base-sleep-time-milliseconds}") int baseSleepTimeMilliseconds,
            @Value("${registry.max-sleep-time-milliseconds}") int maxSleepTimeMilliseconds,
            @Value("${registry.max-retries}") int maxRetries){
        RegistryConfig regConfig = new RegistryConfig();
        regConfig.setName("regConf");
        regConfig.setProtocol(protocol);
        regConfig.setAddress(address);
        regConfig.setName(namespace);
        regConfig.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
        regConfig.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
        regConfig.setMaxRetries(maxRetries);
        return regConfig;
    }
}
