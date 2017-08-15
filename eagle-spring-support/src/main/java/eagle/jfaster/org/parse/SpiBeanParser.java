package eagle.jfaster.org.parse;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by fangyanpeng1 on 2017/8/13.
 */
public class SpiBeanParser extends EagleBeanParser {

    public SpiBeanParser(Class<?> beanClass) {
        super(beanClass);
    }

    @Override
    protected void parse(Element element, BeanDefinitionBuilder beanBuilder, ParserContext parserContext) throws ClassNotFoundException {
        parseInterface(element,beanBuilder);
    }
}
