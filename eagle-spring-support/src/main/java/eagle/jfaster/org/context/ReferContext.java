package eagle.jfaster.org.context;

import eagle.jfaster.org.config.annotation.Refer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangyanpeng on 2017/10/24.
 */
public class ReferContext {

    private static Map<Refer,String> refer2Name = new HashMap<>();

    public static void register(Refer refer,String name){
        refer2Name.put(refer,name);
    }

    public static String getName(Refer refer){
        return refer2Name.get(refer);
    }

}
