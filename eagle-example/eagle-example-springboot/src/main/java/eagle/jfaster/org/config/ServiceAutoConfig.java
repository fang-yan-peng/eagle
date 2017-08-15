package eagle.jfaster.org.config;

import eagle.jfaster.org.bean.ServiceBean;
import eagle.jfaster.org.service.Calculate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class ServiceAutoConfig {

    @Resource(name = "calculate")
    private Calculate calculate;

    @Resource(name="baseService")
    private BaseServiceConfig baseService;

    @Bean
    public ServiceBean<Calculate> getServiceBean(@Value("${service.interface}") String interfaceName) throws ClassNotFoundException {
        ServiceBean<Calculate> serviceBean = new ServiceBean<Calculate>();
        serviceBean.setRef(calculate);
        serviceBean.setBaseService(baseService);
        serviceBean.setInterface((Class<Calculate>) Class.forName(interfaceName));
        return serviceBean;
    }

}
