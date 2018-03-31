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

import com.google.common.base.Strings;

import eagle.jfaster.org.config.ProAndPort;
import eagle.jfaster.org.config.ProtocolConfig;
import eagle.jfaster.org.config.RegistryConfig;
import eagle.jfaster.org.config.ServiceConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.util.CollectionUtil;
import eagle.jfaster.org.util.ConfigUtil;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class ServiceBean<T> extends ServiceConfig<T> implements ApplicationContextAware, InitializingBean, DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext appCtx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }


    @Override
    public void destroy() throws Exception {
        unExport();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkRegistries();
        checkExports();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            try {
                export();
            } catch (Exception e) {
                throw new EagleFrameException(e);
            }
        }
    }

    private void checkRegistries() {
        if (CollectionUtil.isEmpty(getRegistries())) {
            if (getBaseService() != null && !CollectionUtil.isEmpty(getBaseService().getRegistries())) {
                setRegistries(getBaseService().getRegistries());
            }
        }
        List<RegistryConfig> registryConfigs = ConfigUtil.check(getRegistries(), BeanFactoryUtils.beansOfTypeIncludingAncestors(this.appCtx, RegistryConfig.class, false, false), String.format("Error %s not config registries", getInterface().getName()));
        setRegistries(registryConfigs);
    }

    private void checkExports() {
        if (Strings.isNullOrEmpty(getExport())) {
            if (getBaseService() == null || Strings.isNullOrEmpty(getBaseService().getExport())) {
                throw new EagleFrameException("%s not config export", getInterface().getName());
            }
            setExport(getBaseService().getExport());
        }
        Set<ProAndPort> proAndPorts = ConfigUtil.parseExport(getExport());
        Set<ProtocolConfig> protocols = new HashSet<>();
        for (ProAndPort proAndPort : proAndPorts) {
            protocols.add(appCtx.getBean(proAndPort.getProtocolId(), ProtocolConfig.class));
        }
        setProtocols(new ArrayList<>(protocols));
    }
}
