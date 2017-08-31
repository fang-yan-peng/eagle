package eagle.jfaster.org.parse;

import eagle.jfaster.org.config.ConfigEnum;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import static eagle.jfaster.org.util.ParserUtil.register;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class ReferBeanParser extends EagleBeanParser {

    public ReferBeanParser(Class<?> beanClass) {
        super(beanClass);
    }

    @Override
    protected void parse(Element element, BeanDefinitionBuilder beanBuilder, ParserContext parserContext) throws ClassNotFoundException {
        parseInterface(element,beanBuilder);
        String callbackName = element.getAttribute(ConfigEnum.callback.getName());
        register(callbackName,"invokeCallback",beanBuilder,parserContext);
        String mockName = element.getAttribute(ConfigEnum.mock.getName());
        register(mockName,"failMock",beanBuilder,parserContext);
    }
}
