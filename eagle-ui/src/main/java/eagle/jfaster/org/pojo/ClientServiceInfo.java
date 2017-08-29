package eagle.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng on 2017/8/25.
 */
public class ClientServiceInfo implements ServiceCommonSetter {

    @Setter
    @Getter
    String protocol ;

    @Setter
    @Getter
    String serialization ;

    @Setter
    @Getter
    String group ;

    @Setter
    @Getter
    String serviceName ;

    @Setter
    @Getter
    String host ;

    @Setter
    @Getter
    Integer port;

    @Setter
    @Getter
    Integer process;

    @Setter
    @Getter
    String codec ;

    @Setter
    @Getter
    Integer actives ;

    @Setter
    @Getter
    Integer maxContentLength ;

    @Setter
    @Getter
    Long activesWait ;

    @Setter
    @Getter
    Boolean check ;

    @Setter
    @Getter
    Boolean useNative ;

    @Setter
    @Getter
    Boolean compress ;

    @Setter
    @Getter
    Boolean useDefault ;

    @Setter
    @Getter
    String cluster ;

    @Setter
    @Getter
    String heartbeatFactory ;

    @Setter
    @Getter
    Integer heartbeat ;

    @Setter
    @Getter
    String version ;

    @Setter
    @Getter
    String statsLog ;

    @Setter
    @Getter
    Integer retries ;

    @Setter
    @Getter
    Integer callbackThread ;

    @Setter
    @Getter
    Integer callbackQueueSize ;

    @Setter
    @Getter
    Integer callbackWaitTime ;

    @Setter
    @Getter
    Integer requestTimeout ;

    @Setter
    @Getter
    Integer connectTimeout ;

    @Setter
    @Getter
    Long idleTime ;

    @Setter
    @Getter
    Integer minClientConnection ;

    @Setter
    @Getter
    Integer maxClientConnection;

    @Setter
    @Getter
    Integer maxInvokeError ;

    @Setter
    @Getter
    String loadbalance ;

    @Setter
    @Getter
    String haStrategy ;

    @Setter
    @Getter
    Long maxLifetime ;

    @Setter
    @Getter
    String callback;

    @Setter
    @Getter
    String mock;


}
