package eagle.jfaster.org.service;

import eagle.jfaster.org.pojo.ClientServiceInfo;
import eagle.jfaster.org.pojo.ServerServiceInfo;
import eagle.jfaster.org.pojo.ServiceBriefInfo;

import java.util.List;

/**
 * Created by fangyanpeng on 2017/8/24.
 */
public interface InterfaceApiService {

    int getServicesTotalCount();

    List<ServiceBriefInfo> getClientBriefInfos();

    ClientServiceInfo getClientConfig(String serviceName, String protocol, String host);

    boolean deleteClientConfig(String serviceName, String protocol);

    boolean updateClientConfig(ClientServiceInfo serviceInfo);

    List<ServiceBriefInfo> getServerBriefInfos();

    ServerServiceInfo getServerConfig(String serviceName, String protocol, String host);

    boolean deleteServerConfig(String serviceName, String protocol);

    boolean updateServerConfig(ServerServiceInfo serviceInfo);

    boolean disableServer(String serviceName, String protocol, String host);

    boolean enableServer(String serviceName, String protocol, String host);

}
