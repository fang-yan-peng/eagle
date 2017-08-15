package eagle.jfaster.org.config;

import eagle.jfaster.org.bean.ReferBean;
import eagle.jfaster.org.service.Calculate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class ReferAutoConfig {

    @Resource(name="baseRefer")
    private BaseReferConfig baseReferConfig;

    @Bean(name = "calculate1")
    public ReferBean<Calculate> getReferBean(
            @Value("${refer.interface}") String interfaceName,
            @Value("${refer.max-invoke-error}") int maxInvokeError,
            @Value("${refer.max-client-connection}") int maxClientConnection) throws ClassNotFoundException {
        ReferBean<Calculate> referBean = new ReferBean<Calculate>();
        referBean.setInterface((Class<Calculate>) Class.forName(interfaceName));
        referBean.setBaseRefer(baseReferConfig);
        referBean.setMaxInvokeError(maxInvokeError);
        referBean.setMaxClientConnection(maxClientConnection);
        return referBean;
    }
}
