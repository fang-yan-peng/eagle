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

package eagle.jfaster.org.handler;

import eagle.jfaster.org.bean.*;
import eagle.jfaster.org.parse.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class EagleNamespaceHandler extends NamespaceHandlerSupport{

    @Override
    public void init() {
        registerBeanDefinitionParser("refer", new ReferBeanParser(ReferBean.class));
        registerBeanDefinitionParser("service", new ServiceBeanParser(ServiceBean.class));
        registerBeanDefinitionParser("protocol", new EagleBeanParser(ProtocolBean.class));
        registerBeanDefinitionParser("registry", new EagleBeanParser(RegistryBean.class));
        registerBeanDefinitionParser("base-service", new EagleBeanParser(BaseServiceBean.class));
        registerBeanDefinitionParser("base-refer", new EagleBeanParser(BaseReferBean.class));
        registerBeanDefinitionParser("spi", new SpiBeanParser(SpiBean.class));
        registerBeanDefinitionParser("component-scan", new EagleScanBeanParser());
        registerBeanDefinitionParser("trace", new EagleTraceBeanParser());
    }
}
