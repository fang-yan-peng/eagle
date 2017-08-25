package eagle.jfaster.org.pojo;

/**
 * Created by fangyanpeng on 2017/8/25.
 */
public interface ServiceCommonSetter {

    void setServiceName(String serviceName);

    void setHost(String host);

    void setProtocol(String protocol);

    void setPort(Integer port);

    void setProcess(Integer process);

    void setVersion(String version);

    String getServiceName();

    String getHost();

    String getProtocol();

    Integer getPort();

    Integer getProcess();

    String getVersion();
}
