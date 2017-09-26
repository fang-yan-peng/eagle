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
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class ServiceBeanParser extends EagleBeanParser {

    public ServiceBeanParser(Class<?> beanClass) {
        super(beanClass);
    }

    @Override
    protected void parse(Element element, BeanDefinitionBuilder beanBuilder,ParserContext parserContext) throws ClassNotFoundException {
        String ref = element.getAttribute("ref");
        if(Strings.isNullOrEmpty(ref)){
            String clzName = element.getAttribute("class");
            if(Strings.isNullOrEmpty(clzName)){
                throw new EagleFrameException("Error both of ref and class is empty");
            }
            BeanDefinitionBuilder refBuilder = BeanDefinitionBuilder.rootBeanDefinition(clzName);
            parseProperties(element.getChildNodes(), refBuilder);
            String refId = parserContext.getReaderContext().generateBeanName(refBuilder.getBeanDefinition());
            parserContext.getRegistry().registerBeanDefinition(refId,refBuilder.getBeanDefinition());
            beanBuilder.addPropertyReference("ref", refId);
        }
        parseInterface(element,beanBuilder);

    }

    protected void parseProperties(NodeList nodeList, BeanDefinitionBuilder beanBuilder) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("property".equals(node.getNodeName()) || "property".equals(node.getLocalName())) {
                        String name = ((Element) node).getAttribute("name");
                        if (name != null && name.length() > 0) {
                            String value = ((Element) node).getAttribute("value");
                            String ref = ((Element) node).getAttribute("ref");
                            if (value != null && value.length() > 0) {
                                beanBuilder.addPropertyValue(name, value);
                            } else if (ref != null && ref.length() > 0) {
                                beanBuilder.addPropertyValue(name, new RuntimeBeanReference(ref));
                            } else {
                                throw new UnsupportedOperationException("Unsupported <property name=\"" + name
                                        + "\"> sub tag, Only supported <property name=\"" + name + "\" ref=\"...\" /> or <property name=\""
                                        + name + "\" value=\"...\" />");
                            }
                        }
                    }
                }
            }
        }
    }
}
