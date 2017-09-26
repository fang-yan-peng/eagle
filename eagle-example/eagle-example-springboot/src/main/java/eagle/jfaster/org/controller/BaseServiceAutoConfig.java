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

import eagle.jfaster.org.config.BaseServiceConfig;
import eagle.jfaster.org.config.ProtocolConfig;
import eagle.jfaster.org.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class BaseServiceAutoConfig {

    @Resource(name = "protoConf")
    private ProtocolConfig protocol;

    @Resource(name="regConf")
    private RegistryConfig reg;

    @Bean(name = "baseService")
    public BaseServiceConfig getBaseServiceConfig(
            @Value("${base-service.group}") String group,
            @Value("${base-service.export}") String export){
        BaseServiceConfig bsConfig = new BaseServiceConfig();
        List<ProtocolConfig> protos = new ArrayList<ProtocolConfig>();
        protos.add(protocol);
        List<RegistryConfig> regConfigs = new ArrayList<RegistryConfig>();
        regConfigs.add(reg);
        bsConfig.setRegistries(regConfigs);
        bsConfig.setProtocols(protos);
        bsConfig.setExport(export);
        bsConfig.setGroup(group);
        return bsConfig;
    }
}
