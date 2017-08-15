package eagle.jfaster.org.handler;

import eagle.jfaster.org.bean.*;
import eagle.jfaster.org.parse.EagleBeanParser;
import eagle.jfaster.org.parse.ReferBeanParser;
import eagle.jfaster.org.parse.ServiceBeanParser;
import eagle.jfaster.org.parse.SpiBeanParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class EagleNamespaceHandler extends NamespaceHandlerSupport{

    @Override
    public void init() {
        registerBeanDefinitionParser("refer", new ReferBeanParser(ReferBean.class));
        registerBeanDefinitionParser("service", new ServiceBeanParser(ServiceBean.class));
        registerBeanDefinitionParser("protocol", new EagleBeanParser(ProtocolBean.class));
        registerBeanDefinitionParser("registry", new EagleBeanParser(RegistryBean.class));
        registerBeanDefinitionParser("base-service", new EagleBeanParser(BaseServiceBean.class));
        registerBeanDefinitionParser("base-refer", new EagleBeanParser(BaseReferBean.class));
        registerBeanDefinitionParser("spi", new SpiBeanParser(SpiBean.class));
    }
}
