package eagle.jfaster.org.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class BaseReferAutoConfig {

    @Resource(name = "protoConf")
    private ProtocolConfig protocol;

    @Resource(name="regConf")
    private RegistryConfig reg;

    @Bean(name = "baseRefer")
    public BaseReferConfig getBaseServiceConfig(
            @Value("${base-refer.request-timeout}") int requestTimeout,
            @Value("${base-refer.actives}") int actives,
            @Value("${base-refer.actives-wait}") long activesWait,
            @Value("${base-refer.loadbalance}") String loadbalance,
            @Value("${base-refer.compress}") boolean compress,
            @Value("${base-refer.group}") String group,
            @Value("${base-refer.connect-timeout}") long connectTimeout){
        BaseReferConfig baseReferConfig = new BaseReferConfig();
        List<ProtocolConfig> protos = new ArrayList<ProtocolConfig>();
        protos.add(protocol);
        List<RegistryConfig> regConfigs = new ArrayList<RegistryConfig>();
        regConfigs.add(reg);
        baseReferConfig.setProtocols(protos);
        baseReferConfig.setRegistries(regConfigs);
        baseReferConfig.setGroup(group);
        baseReferConfig.setRequestTimeout(requestTimeout);
        baseReferConfig.setActives(actives);
        baseReferConfig.setActivesWait(activesWait);
        baseReferConfig.setLoadbalance(loadbalance);
        baseReferConfig.setCompress(compress);
        baseReferConfig.setConnectTimeout(connectTimeout);
        return baseReferConfig;
    }
}
