package eagle.jfaster.org.bean;

import eagle.jfaster.org.config.SpiConfig;
import eagle.jfaster.org.spi.SpiClassLoader;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by fangyanpeng1 on 2017/8/13.
 */
public class SpiBean<T> extends SpiConfig<T> implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        SpiClassLoader.getClassLoader(getInterface()).addExtensionClass(getSpiClass());
    }
}
