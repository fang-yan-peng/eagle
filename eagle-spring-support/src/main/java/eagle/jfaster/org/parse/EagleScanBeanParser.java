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

package eagle.jfaster.org.parse;

import com.google.common.base.Strings;
import eagle.jfaster.org.bean.ReferBean;
import eagle.jfaster.org.bean.ServiceBean;
import eagle.jfaster.org.config.annotation.Refer;
import eagle.jfaster.org.config.annotation.Service;
import eagle.jfaster.org.context.ReferContext;
import eagle.jfaster.org.exception.EagleFrameException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.w3c.dom.Element;

import java.lang.reflect.Field;

import static eagle.jfaster.org.util.ParserUtil.multiRef;
import static eagle.jfaster.org.util.ParserUtil.register;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
public class EagleScanBeanParser extends AbstractScanBeanParser {

    @Override
    public void registerCandidateComponents(Element element, String basePackage, ParserContext parserContext) {
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage,parserContext.getDelegate().getEnvironment()) + "/" + this.resourcePattern;
            Resource[] rs = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource r : rs) {
                MetadataReader reader = metadataReaderFactory.getMetadataReader(r);
                AnnotationMetadata annotationMD = reader.getAnnotationMetadata();
                ClassMetadata clazzMD = reader.getClassMetadata();
                Class<?> clz = Class.forName(clazzMD.getClassName());
                if (annotationMD.hasAnnotation(Service.class.getName())) {
                    if(clz.isInterface()){
                        throw new EagleFrameException("registried service should not be a interface");
                    }
                    Service serviceAnnotation = clz.getAnnotation(Service.class);
                    GenericBeanDefinition bf = new GenericBeanDefinition();
                    bf.setBeanClass(clz);
                    bf.setLazyInit(false);
                    String serviceId = Strings.isNullOrEmpty(serviceAnnotation.id()) ? parserContext.getReaderContext().generateBeanName(bf) : serviceAnnotation.id();
                    //注册实现类
                    parserContext.getRegistry().registerBeanDefinition(serviceId,bf);
                    Class<?>[] interfaces = clz.getInterfaces();
                    for(Class<?> interfaceName : interfaces){
                        //注册ServiceBean
                        BeanDefinitionBuilder service = BeanDefinitionBuilder.rootBeanDefinition(ServiceBean.class);
                        service.setLazyInit(false);
                        service.addPropertyValue("interface",interfaceName);
                        service.addPropertyReference("ref",serviceId);
                        if(Strings.isNullOrEmpty(serviceAnnotation.export())){
                            throw new EagleFrameException("export is not config");
                        }
                        service.addPropertyValue("export",serviceAnnotation.export());
                        if(!Strings.isNullOrEmpty(serviceAnnotation.registry())){
                            multiRef("registries",serviceAnnotation.registry(),service);
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.protocol())){
                            multiRef("protocols",serviceAnnotation.protocol(),service);
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.statsLog())){
                            service.addPropertyValue("statsLog",serviceAnnotation.statsLog());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.actives())){
                            service.addPropertyValue("actives",serviceAnnotation.actives());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.activesWait())){
                            service.addPropertyValue("activesWait",serviceAnnotation.activesWait());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.application())){
                            service.addPropertyValue("application",serviceAnnotation.application());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.group())){
                            service.addPropertyValue("group",serviceAnnotation.group());
                        }

                        if(!Strings.isNullOrEmpty(serviceAnnotation.baseService())){
                            service.addPropertyReference("baseService",serviceAnnotation.baseService());
                        }

                        if(!Strings.isNullOrEmpty(serviceAnnotation.filter())){
                            service.addPropertyValue("filter",serviceAnnotation.filter());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.host())){
                            service.addPropertyValue("host",serviceAnnotation.host());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.module())){
                            service.addPropertyValue("module",serviceAnnotation.module());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.mock())){
                            service.addPropertyValue("mock",serviceAnnotation.mock());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.retries())){
                            service.addPropertyValue("retries",serviceAnnotation.retries());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.register())){
                            service.addPropertyValue("register",serviceAnnotation.register());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.weight())){
                            service.addPropertyValue("weight",serviceAnnotation.weight());
                        }
                        if(!Strings.isNullOrEmpty(serviceAnnotation.version())){
                            service.addPropertyValue("version",serviceAnnotation.version());
                        }
                        parserContext.getRegistry().registerBeanDefinition(parserContext.getReaderContext().generateBeanName(service.getBeanDefinition()),service.getBeanDefinition());


                    }
                }

                if(clz.isInterface() || !(clz.isAnnotationPresent(Service.class) || clz.isAnnotationPresent(org.springframework.stereotype.Service.class))){
                    continue;
                }

                Field[] fields;
                try {
                    fields = clz.getDeclaredFields();
                    if(fields == null || fields.length == 0){
                        continue;
                    }
                } catch (Throwable e) {
                    continue;
                }

                for (Field field : fields){
                    if(field.isAnnotationPresent(Refer.class)){
                        Refer referAnnotation = field.getAnnotation(Refer.class);
                        if(ReferContext.getName(referAnnotation) != null){
                            continue;
                        }
                        Class<?> interfaceClass = field.getType();
                        if(!interfaceClass.isInterface()){
                            throw new EagleFrameException("Refer should be annotated on a interface");
                        }
                        BeanDefinitionBuilder refer = BeanDefinitionBuilder.rootBeanDefinition(ReferBean.class);
                        refer.setLazyInit(false);
                        if(!Strings.isNullOrEmpty(referAnnotation.registry())){
                            multiRef("registries",referAnnotation.registry(),refer);
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.protocol())){
                            multiRef("protocols",referAnnotation.protocol(),refer);
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.statsLog())){
                            refer.addPropertyValue("statsLog",referAnnotation.statsLog());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.actives())){
                            refer.addPropertyValue("actives",referAnnotation.actives());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.activesWait())){
                            refer.addPropertyValue("activesWait",referAnnotation.activesWait());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.application())){
                            refer.addPropertyValue("application",referAnnotation.application());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.group())){
                            refer.addPropertyValue("group",referAnnotation.group());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.baseRefer())){
                            refer.addPropertyReference("baseRefer",referAnnotation.baseRefer());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.filter())){
                            refer.addPropertyValue("filter",referAnnotation.filter());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.host())){
                            refer.addPropertyValue("host",referAnnotation.host());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.module())){
                            refer.addPropertyValue("module",referAnnotation.module());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.mock())){
                            refer.addPropertyValue("mock",referAnnotation.mock());
                            register(referAnnotation.mock(),"failMock",refer,parserContext);
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.retries())){
                            refer.addPropertyValue("retries",referAnnotation.retries());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.register())){
                            refer.addPropertyValue("register",referAnnotation.register());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.version())){
                            refer.addPropertyValue("version",referAnnotation.version());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.compress())){
                            refer.addPropertyValue("compress",referAnnotation.compress());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.connectTimeout())){
                            refer.addPropertyValue("connectTimeout",referAnnotation.connectTimeout());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.haStrategy())){
                            refer.addPropertyValue("haStrategy",referAnnotation.haStrategy());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.check())){
                            refer.addPropertyValue("check",referAnnotation.check());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.idleTime())){
                            refer.addPropertyValue("idleTime",referAnnotation.idleTime());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.subscribe())){
                            refer.addPropertyValue("subscribe",referAnnotation.subscribe());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.maxClientConnection())){
                            refer.addPropertyValue("maxClientConnection",referAnnotation.maxClientConnection());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.minClientConnection())){
                            refer.addPropertyValue("minClientConnection",referAnnotation.minClientConnection());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.maxLifetime())){
                            refer.addPropertyValue("maxLifetime",referAnnotation.maxLifetime());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.minClientConnection())){
                            refer.addPropertyValue("minClientConnection",referAnnotation.minClientConnection());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.loadbalance())){
                            refer.addPropertyValue("loadbalance",referAnnotation.loadbalance());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.maxInvokeError())){
                            refer.addPropertyValue("maxInvokeError",referAnnotation.maxInvokeError());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.minCompressSize())){
                            refer.addPropertyValue("minCompressSize",referAnnotation.minCompressSize());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.requestTimeout())){
                            refer.addPropertyValue("requestTimeout",referAnnotation.requestTimeout());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.proxy())){
                            refer.addPropertyValue("proxy",referAnnotation.proxy());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.callbackThread())){
                            refer.addPropertyValue("callbackThread",referAnnotation.callbackThread());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.callbackQueueSize())){
                            refer.addPropertyValue("callbackQueueSize",referAnnotation.callbackQueueSize());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.callbackWaitTime())){
                            refer.addPropertyValue("callbackWaitTime",referAnnotation.callbackWaitTime());
                        }
                        if(!Strings.isNullOrEmpty(referAnnotation.callback())){
                            refer.addPropertyValue("callback",referAnnotation.callback());
                            register(referAnnotation.callback(),"invokeCallback",refer,parserContext);
                        }
                        refer.addPropertyValue("interface",interfaceClass);
                        String referId = String.format("%s.%s",clz.getCanonicalName(),field.getName());
                        ReferContext.register(referAnnotation,referId);
                        parserContext.getRegistry().registerBeanDefinition(referId,refer.getBeanDefinition());
                    }
                }
            }
        }catch (Exception ex){
            throw new EagleFrameException("I/O failure during classpath scanning %s", ex.getMessage());
        }
    }

}
