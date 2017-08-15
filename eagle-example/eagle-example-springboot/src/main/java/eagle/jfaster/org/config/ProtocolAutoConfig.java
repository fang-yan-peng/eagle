package eagle.jfaster.org.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@Configuration
public class ProtocolAutoConfig {

    @Bean(name = "protoConf")
    public ProtocolConfig getProtocolConfig(
            @Value("${protocol.name}") String eagle,
            @Value("${protocol.serialization}") String serialization,
            @Value("${protocol.use-default}") boolean useDefault,
            @Value("${protocol.max-content-length}") int maxContentLength,
            @Value("${protocol.max-server-connection}") int maxServerConnection,
            @Value("${protocol.core-worker-thread}") int coreWorkerThread,
            @Value("${protocol.max-worker-thread}") int maxWorkerThread,
            @Value("${protocol.worker-queue-size}") int workerQueueSize){
        ProtocolConfig protoConfig = new ProtocolConfig();
        protoConfig.setId("protoConf");
        protoConfig.setName(eagle);
        protoConfig.setSerialization(serialization);
        protoConfig.setUseDefault(useDefault);
        protoConfig.setMaxContentLength(maxContentLength);
        protoConfig.setMaxServerConnection(maxServerConnection);
        protoConfig.setCoreWorkerThread(coreWorkerThread);
        protoConfig.setMaxWorkerThread(maxWorkerThread);
        protoConfig.setWorkerQueueSize(workerQueueSize);
        return protoConfig;
    }
}
