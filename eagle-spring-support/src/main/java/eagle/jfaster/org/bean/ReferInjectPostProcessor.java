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
import eagle.jfaster.org.config.annotation.Refer;
import eagle.jfaster.org.context.ReferContext;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.util.AopTargetUtil;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Created by fangyanpeng on 2017/10/24.
 */
public class ReferInjectPostProcessor implements BeanPostProcessor,ApplicationContextAware {

    private ConfigurableApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            final Class<?> targetClass = AopTargetUtil.getTargetClass(bean);
            final Object targetBean = AopTargetUtil.getTarget(bean);
            if(AopUtils.isAopProxy(targetBean)){
                return bean;
            }
            try {
                ReflectionUtils.doWithFields(targetClass,new ReflectionUtils.FieldCallback(){

                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        Refer refer = field.getAnnotation(Refer.class);
                        ReflectionUtils.makeAccessible(field);
                        String id = ReferContext.getName(refer,field.getType());
                        if(!Strings.isNullOrEmpty(id) && field.get(targetBean) == null){
                            field.set(targetBean,ctx.getBean(id));
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
        } catch (Exception e) {
            throw new EagleFrameException(e);
        }
        return bean;
    }
}
