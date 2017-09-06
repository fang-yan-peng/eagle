package eagle.jfaster.org.bean;
import eagle.jfaster.org.config.ProtocolConfig;
import eagle.jfaster.org.config.ReferConfig;
import eagle.jfaster.org.config.RegistryConfig;
import eagle.jfaster.org.util.CollectionUtil;
import eagle.jfaster.org.util.ConfigUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class ReferBean<T> extends ReferConfig<T> implements ApplicationContextAware, FactoryBean<T>, InitializingBean, DisposableBean {

    private ApplicationContext appCtx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        unRef();
    }

    @Override
    public T getObject() throws Exception {
        return getRef();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterface();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkRegistries();
        checkProtocols();
    }

    private void checkRegistries(){
        if(CollectionUtil.isEmpty(getRegistries())){
            if(getBaseRefer() != null && !CollectionUtil.isEmpty(getBaseRefer().getRegistries())){
                setRegistries(getBaseRefer().getRegistries());
            }
        }
        List<RegistryConfig> registryConfigs = ConfigUtil.check(getRegistries(),BeanFactoryUtils.beansOfTypeIncludingAncestors(this.appCtx, RegistryConfig.class, false, false),String.format("Error %s not config registries",getInterface().getName()));
        setRegistries(registryConfigs);
    }

    private void checkProtocols(){
        if(CollectionUtil.isEmpty(getProtocols())){
            if(getBaseRefer() != null && !CollectionUtil.isEmpty(getBaseRefer().getProtocols())){
                setProtocols(getBaseRefer().getProtocols());
            }
        }
        List<ProtocolConfig> protocolConfigs = ConfigUtil.check(getProtocols(),BeanFactoryUtils.beansOfTypeIncludingAncestors(this.appCtx, ProtocolConfig.class, false, false),String.format("Error %s not config protocols",getInterface().getName()));
        setProtocols(protocolConfigs);
    }

}
