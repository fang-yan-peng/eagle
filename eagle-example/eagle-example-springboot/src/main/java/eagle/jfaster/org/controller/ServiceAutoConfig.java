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

import eagle.jfaster.org.bean.ServiceBean;
import eagle.jfaster.org.config.BaseServiceConfig;
import eagle.jfaster.org.service.Calculate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class ServiceAutoConfig {

    @Resource(name = "calculate")
    private Calculate calculate;

    @Resource(name="baseService")
    private BaseServiceConfig baseService;

    @Bean
    public ServiceBean<Calculate> getServiceBean(@Value("${service.interface}") String interfaceName) throws ClassNotFoundException {
        ServiceBean<Calculate> serviceBean = new ServiceBean<Calculate>();
        serviceBean.setRef(calculate);
        serviceBean.setBaseService(baseService);
        serviceBean.setInterface((Class<Calculate>) Class.forName(interfaceName));
        return serviceBean;
    }

}
