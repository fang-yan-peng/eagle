package eagle.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng on 2017/8/25.
 */
public class ServiceBriefInfo{

    @Setter
    @Getter
    private String serviceName;

    @Setter
    @Getter
    private String protocol;

    @Setter
    @Getter
    private String version;

    @Setter
    @Getter
    private String group;

    @Setter
    @Getter
    private String host;

    @Setter
    @Getter
    private Integer process;

    @Setter
    @Getter
    private ServiceStatus status;


    public enum ServiceStatus {
        OK,
        CRASHED,
        DISABLED,
        SHARDING_FLAG
    }
}
