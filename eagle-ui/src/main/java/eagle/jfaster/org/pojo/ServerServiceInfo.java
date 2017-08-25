package eagle.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng on 2017/8/25.
 */
public class ServerServiceInfo implements ServiceCommonSetter{

    @Setter
    @Getter
    String protocol;

    @Setter
    @Getter
    String serialization;

    @Setter
    @Getter
    String group;

    @Setter
    @Getter
    String serviceName;

    @Setter
    @Getter
    String host;

    @Setter
    @Getter
    Integer port;

    @Setter
    @Getter
    String codec;

    @Setter
    @Getter
    Integer maxContentLength;

    @Setter
    @Getter
    Boolean useNative;

    @Setter
    @Getter
    String heartbeatFactory;

    @Setter
    @Getter
    String version;

    @Setter
    @Getter
    Integer weight;

    @Setter
    @Getter
    Integer selectThreadSize;

    @Setter
    @Getter
    Integer maxServerConnection;

    @Setter
    @Getter
    Integer coreWorkerThread;

    @Setter
    @Getter
    Integer maxWorkerThread;

    @Setter
    @Getter
    Integer workerQueueSize;

    @Override
    public void setProcess(Integer process) {

    }

    @Override
    public Integer getProcess() {
        return null;
    }
}
