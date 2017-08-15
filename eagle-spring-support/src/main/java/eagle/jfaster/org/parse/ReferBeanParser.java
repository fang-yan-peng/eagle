package eagle.jfaster.org.parse;

import com.google.common.base.Strings;
import eagle.jfaster.org.config.ConfigEnum;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

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
        if(!Strings.isNullOrEmpty(callbackName)){
            BeanDefinitionBuilder callBuilder = BeanDefinitionBuilder.rootBeanDefinition(callbackName);
            String callBackId = parserContext.getReaderContext().generateBeanName(callBuilder.getBeanDefinition());
            parserContext.getRegistry().registerBeanDefinition(callBackId,callBuilder.getBeanDefinition());
            beanBuilder.addPropertyReference("invokeCallBack", callBackId);
        }
    }
}
