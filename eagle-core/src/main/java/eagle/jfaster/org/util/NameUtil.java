package eagle.jfaster.org.util;


/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class NameUtil {


    public static final char UNDERLINE='-';

    public static String camel2Middleline(String param){
        if (param == null || "".equals(param.trim())){
            return "";
        }
        int len = param.length();
        StringBuilder sb= new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)){
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
    public static String middleline2Camel(String param){
        if (param == null || "".equals(param.trim())){
            return "";
        }
        int len=param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE){
                if (++i < len){
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
}