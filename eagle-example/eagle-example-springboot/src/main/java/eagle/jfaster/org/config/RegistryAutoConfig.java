package eagle.jfaster.org.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class RegistryAutoConfig {

    @Bean(name = "regConf")
    public RegistryConfig getRegistryConfig(
            @Value("${registry.protocol}") String protocol,
            @Value("${registry.address}") String address,
            @Value("${registry.namespace}") String namespace,
            @Value("${registry.base-sleep-time-milliseconds}") int baseSleepTimeMilliseconds,
            @Value("${registry.max-sleep-time-milliseconds}") int maxSleepTimeMilliseconds,
            @Value("${registry.max-retries}") int maxRetries){
        RegistryConfig regConfig = new RegistryConfig();
        regConfig.setName("regConf");
        regConfig.setProtocol(protocol);
        regConfig.setAddress(address);
        regConfig.setName(namespace);
        regConfig.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
        regConfig.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
        regConfig.setMaxRetries(maxRetries);
        return regConfig;
    }
}
