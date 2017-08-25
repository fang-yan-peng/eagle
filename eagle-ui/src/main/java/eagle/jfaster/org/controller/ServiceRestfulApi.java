package eagle.jfaster.org.controller;

import eagle.jfaster.org.pojo.ClientServiceInfo;
import eagle.jfaster.org.pojo.ServerServiceInfo;
import eagle.jfaster.org.pojo.ServiceBriefInfo;
import eagle.jfaster.org.service.InterfaceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;


/**
 * @author fangyanpeng
 */
@RestController
@RequestMapping("/service")
public class ServiceRestfulApi {

    @Resource
    private InterfaceService iService;

    @RequestMapping("/count")
    public int getServiceCnt(){
        return iService.getApiService().getServicesTotalCount();
    }

    @RequestMapping("/getClientsBriefInfo")
    public Collection<ServiceBriefInfo> getClientsBriefInfo(){
        return iService.getApiService().getClientBriefInfos();
    }


    @RequestMapping("/client/config")
    public ClientServiceInfo getClientConfig(@RequestParam("host") String host, @RequestParam("serviceName") String serviceName, @RequestParam("protocol") String protocol){

        return iService.getApiService().getClientConfig(serviceName,protocol,host);
    }

    @RequestMapping(value = "/client/config/delete")
    public boolean deleteClientConfig(@RequestParam("serviceName") String serviceName, @RequestParam("protocol") String protocol){
        return iService.getApiService().deleteClientConfig(serviceName,protocol);
    }

    @RequestMapping(value = "/client/config/update",method = RequestMethod.PUT)
    public boolean deleteClientConfig(@RequestBody ClientServiceInfo serviceInfo){
        return iService.getApiService().updateClientConfig(serviceInfo);
    }

    @RequestMapping("/getServersBriefInfo")
    public Collection<ServiceBriefInfo> getServersBriefInfo(){
        return iService.getApiService().getServerBriefInfos();
    }


    @RequestMapping("/server/config")
    public ServerServiceInfo getServerConfig(@RequestParam("host") String host, @RequestParam("serviceName") String serviceName, @RequestParam("protocol") String protocol){
        return iService.getApiService().getServerConfig(serviceName,protocol,host);
    }

    @RequestMapping(value = "/server/config/delete")
    public boolean deleteServertConfig(@RequestParam("serviceName") String serviceName, @RequestParam("protocol") String protocol){
        return iService.getApiService().deleteServerConfig(serviceName,protocol);
    }

    @RequestMapping(value = "/server/config/update",method = RequestMethod.PUT)
    public boolean deleteServerConfig(@RequestBody ServerServiceInfo serviceInfo){
        return iService.getApiService().updateServerConfig(serviceInfo);
    }

    @RequestMapping(value = "/server/disable")
    public boolean disableServertConfig(@RequestParam("serviceName") String serviceName, @RequestParam("protocol") String protocol,@RequestParam("host") String host){
        return iService.getApiService().disableServer(serviceName,protocol,host);
    }

    @RequestMapping(value = "/server/enable")
    public boolean enableServertConfig(@RequestParam("serviceName") String serviceName, @RequestParam("protocol") String protocol,@RequestParam("host") String host){
        return iService.getApiService().enableServer(serviceName,protocol,host);
    }



}
