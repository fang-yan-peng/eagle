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
public class BaseServiceAutoConfig {

    @Resource(name = "protoConf")
    private ProtocolConfig protocol;

    @Resource(name="regConf")
    private RegistryConfig reg;

    @Bean(name = "baseService")
    public BaseServiceConfig getBaseServiceConfig(
            @Value("${base-service.group}") String group,
            @Value("${base-service.export}") String export){
        BaseServiceConfig bsConfig = new BaseServiceConfig();
        List<ProtocolConfig> protos = new ArrayList<ProtocolConfig>();
        protos.add(protocol);
        List<RegistryConfig> regConfigs = new ArrayList<RegistryConfig>();
        regConfigs.add(reg);
        bsConfig.setRegistries(regConfigs);
        bsConfig.setProtocols(protos);
        bsConfig.setExport(export);
        bsConfig.setGroup(group);
        return bsConfig;
    }
}
