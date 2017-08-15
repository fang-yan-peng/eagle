package eagle.jfaster.org.util;

import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;

/**
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
public class ExploreUtil {

    public static final String SERVICE_KEY_FORMAT = "%s-%s";

    public static String getServiceKey(MergeConfig config){
        return String.format(SERVICE_KEY_FORMAT,config.getInterfaceName(),config.getVersion());
    }

    public static String getServiceKey(Request request){
        return String.format(SERVICE_KEY_FORMAT,request.getInterfaceName(),getVersion(request));
    }

    public static String getVersion(Request request){
        String version = ConfigEnum.version.getValue();
        if(request.getAttachments() != null && request.getAttachments().containsKey(ConfigEnum.version.name())){
            request.getAttachments().get(ConfigEnum.version.name());
        }
        return version;
    }
}
