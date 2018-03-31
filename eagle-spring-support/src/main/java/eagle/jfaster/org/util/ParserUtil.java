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

package eagle.jfaster.org.util;

import com.google.common.base.Strings;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
public class ParserUtil {

    public static void multiRef(String name, String value, BeanDefinitionBuilder beanBuiler) {
        String[] values = value.split("\\s*[,]+\\s*");
        if (values == null || values.length == 0) {
            return;
        }
        ManagedList list = new ManagedList();
        for (String v : values) {
            if (!Strings.isNullOrEmpty(v)) {
                list.add(new RuntimeBeanReference(v));
            }
        }
        beanBuiler.addPropertyValue(name, list);
    }

    public static void register(String beanClassName, String propertyName, BeanDefinitionBuilder beanBuilder, ParserContext parserContext) {
        if (!Strings.isNullOrEmpty(beanClassName)) {
            BeanDefinitionBuilder injectBuilder = BeanDefinitionBuilder.rootBeanDefinition(beanClassName);
            String injectId = parserContext.getReaderContext().generateBeanName(injectBuilder.getBeanDefinition());
            parserContext.getRegistry().registerBeanDefinition(injectId, injectBuilder.getBeanDefinition());
            beanBuilder.addPropertyReference(propertyName, injectId);
        }
    }
}
