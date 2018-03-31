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

package eagle.jfaster.org.bean;

import eagle.jfaster.org.config.ProtocolConfig;
import eagle.jfaster.org.config.ReferConfig;
import eagle.jfaster.org.config.RegistryConfig;
import eagle.jfaster.org.util.CollectionUtil;
import eagle.jfaster.org.util.ConfigUtil;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class ReferBean<T> extends ReferConfig<T> implements ApplicationContextAware, FactoryBean<T>, InitializingBean, DisposableBean {

    private ApplicationContext appCtx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        unRef();
    }

    @Override
    public T getObject() throws Exception {
        return getRef();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterface();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkRegistries();
        checkProtocols();
    }

    private void checkRegistries() {
        if (CollectionUtil.isEmpty(getRegistries())) {
            if (getBaseRefer() != null && !CollectionUtil.isEmpty(getBaseRefer().getRegistries())) {
                setRegistries(getBaseRefer().getRegistries());
            }
        }
        List<RegistryConfig> registryConfigs = ConfigUtil.check(getRegistries(), BeanFactoryUtils.beansOfTypeIncludingAncestors(this.appCtx, RegistryConfig.class, false, false), String.format("Error %s not config registries", getInterface().getName()));
        setRegistries(registryConfigs);
    }

    private void checkProtocols() {
        if (CollectionUtil.isEmpty(getProtocols())) {
            if (getBaseRefer() != null && !CollectionUtil.isEmpty(getBaseRefer().getProtocols())) {
                setProtocols(getBaseRefer().getProtocols());
            }
        }
        List<ProtocolConfig> protocolConfigs = ConfigUtil.check(getProtocols(), BeanFactoryUtils.beansOfTypeIncludingAncestors(this.appCtx, ProtocolConfig.class, false, false), String.format("Error %s not config protocols", getInterface().getName()));
        setProtocols(protocolConfigs);
    }

}
