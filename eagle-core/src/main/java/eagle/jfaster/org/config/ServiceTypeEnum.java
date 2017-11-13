package eagle.jfaster.org.config;

import com.google.common.base.Strings;

/**
 * Created by fangyanpeng on 2017/11/13.
 */

public enum ServiceTypeEnum {

    JDK("jdk"),CGLIB("cglib");

    ServiceTypeEnum(String type){
        this.type = type;
    }

    public String type;

    public static ServiceTypeEnum typeOf(String type){
        if(Strings.isNullOrEmpty(type)){
            return JDK;
        }
        for (ServiceTypeEnum typeEnum : ServiceTypeEnum.values()){
            if(typeEnum.type.equalsIgnoreCase(type)){
                return typeEnum;
            }
        }
        return JDK;
    }

}
