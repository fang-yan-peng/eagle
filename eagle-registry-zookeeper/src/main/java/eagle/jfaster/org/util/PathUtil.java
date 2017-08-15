package eagle.jfaster.org.util;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
public class PathUtil {

    private static final String SUFFIX = "service";

    private static final String FULL_PATH = "%s/%s";

    public static String getServicePath(String path){
        int pos = path.lastIndexOf("/");
        if(pos < 0){
            return null;
        }
        String servicePath = path.substring(0,pos);
        return servicePath.endsWith(SUFFIX) ? servicePath : null;
    }

    public static String getHostByPath(String path){
        int pos = path.lastIndexOf("/");
        if(pos < 0 || pos == path.length() -1){
            return null;
        }
        return path.substring(pos+1);
    }

    public static String getFullPath(String servicePath,String host){
        return String.format(FULL_PATH,servicePath,host);
    }

}
