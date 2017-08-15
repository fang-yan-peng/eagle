package eagle.jfaster.org.util;

import java.util.Collection;
import java.util.Map;

/**
 * Created by fangyanpeng1 on 2017/8/6.
 */
public class CollectionUtil {

    public  static  boolean isEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public  static  boolean isEmpty(Map map){
        return map == null || map.isEmpty();
    }


}
