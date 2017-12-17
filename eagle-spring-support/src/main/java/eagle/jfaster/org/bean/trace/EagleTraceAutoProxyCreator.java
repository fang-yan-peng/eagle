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
package eagle.jfaster.org.bean.trace;

import com.google.common.base.Strings;
import eagle.jfaster.org.config.annotation.Refer;
import eagle.jfaster.org.context.ReferContext;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.trace.annotation.Trace;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by fangyanpeng on 2017/12/16.
 */
public class EagleTraceAutoProxyCreator extends AbstractAutoProxyCreator implements ApplicationContextAware{

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EagleTraceAutoProxyCreator.class);

    private ApplicationContext ctx;

    public EagleTraceAutoProxyCreator() {
        //设置顺序在spring的aop代理之前执行
        super.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
    }

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> aClass, String s, TargetSource targetSource) throws BeansException {
        if(!needTrace(aClass)){
            return DO_NOT_PROXY;
        }
        return new Object[0];
    }


    @Override
    protected Object createProxy(
            Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setAopProxyFactory(new EagleTraceProxyFactory());
        proxyFactory.copyFrom(this);
        if (!proxyFactory.isProxyTargetClass()) {
            if (shouldProxyTargetClass(beanClass, beanName)) {
                proxyFactory.setProxyTargetClass(true);
            }
            else {
                evaluateProxyInterfaces(beanClass, proxyFactory);
            }
        }
        proxyFactory.setTargetSource(targetSource);
        customizeProxyFactory(proxyFactory);
        proxyFactory.setFrozen(false);
        injectRefer(beanClass,targetSource);
        return proxyFactory.getProxy(getProxyClassLoader());

    }

    private void injectRefer(final Class<?> beanClass, final TargetSource bean){
        try {
            ReflectionUtils.doWithFields(beanClass,new ReflectionUtils.FieldCallback(){

                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    Refer refer = field.getAnnotation(Refer.class);
                    ReflectionUtils.makeAccessible(field);
                    String id = ReferContext.getName(refer,field.getType());
                    if(!Strings.isNullOrEmpty(id)){
                        try {
                            field.set(bean.getTarget(),ctx.getBean(id));
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }

                }
            },new ReflectionUtils.FieldFilter(){

                @Override
                public boolean matches(Field field) {
                    return field.isAnnotationPresent(Refer.class);
                }
            });
        } catch (Throwable e) {

        }
    }

    private boolean needTrace(Class<?> aClass){
        boolean traceAllMethods = aClass.isAnnotationPresent(Trace.class);
        boolean trace = traceAllMethods;
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(aClass);
        for (Method method : methods){
            if(traceAllMethods || method.isAnnotationPresent(Trace.class)){
                if(!trace){
                    trace = true;
                }
                EagleTraceMethodRecods.recordTrace(method,aClass,true);
            }else {
                EagleTraceMethodRecods.recordTrace(method,aClass,false);
            }
        }
        return trace;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
