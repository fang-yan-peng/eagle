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

import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.util.NameUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static eagle.jfaster.org.util.ParserUtil.multiRef;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
@RequiredArgsConstructor
public class EagleBeanParser extends AbstractBeanDefinitionParser {

    protected final Class<?> beanClass;

    private static final Map<String, String> n2n = new HashMap() {{
        this.put("protocols", "protocol");
        this.put("registries", "registry");
        this.put("interceptors", "interceptor");
    }};

    private static final Set<String> ns = new HashSet() {{
        this.add("baseService");
        this.add("baseRefer");
        this.add("ref");
    }};

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        try {
            BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(beanClass);
            PropertyDescriptor[] pds = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                String name = pd.getName();
                name = n2n.containsKey(name) ? n2n.get(name) : name;
                String property = NameUtil.camel2Middleline(name);
                String value = element.getAttribute(property);
                if (Strings.isNullOrEmpty(value)) {
                    continue;
                }
                if (n2n.containsKey(pd.getName())) {
                    multiRef(pd.getName(), value, beanBuilder);
                } else if (ns.contains(name)) {
                    beanBuilder.addPropertyReference(name, value);
                } else {
                    beanBuilder.addPropertyValue(name, value);
                }
            }
            parse(element, beanBuilder, parserContext);
            return beanBuilder.getBeanDefinition();
        } catch (Exception e) {
            throw new EagleFrameException(e);
        }
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        String id = element.getAttribute("id");
        if (!Strings.isNullOrEmpty(id)) {
            return id;
        }
        id = element.getAttribute("name");
        if (!Strings.isNullOrEmpty(id)) {
            return id;
        }
        return parserContext.getReaderContext().generateBeanName(definition);
    }

    protected void parseInterface(Element element, BeanDefinitionBuilder beanBuilder) throws ClassNotFoundException {
        String interfaceName = element.getAttribute("interface");
        if (Strings.isNullOrEmpty(interfaceName)) {
            throw new EagleFrameException("Error not config interface");
        }
        Class interfaceClz = ClassUtils.forName(interfaceName, Thread.currentThread().getContextClassLoader());
        beanBuilder.addPropertyValue("interface", interfaceClz);
    }

    protected void parse(Element element, BeanDefinitionBuilder beanBuilder, ParserContext parserContext) throws ClassNotFoundException {
    }

}
