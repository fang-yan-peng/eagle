package eagle.jfaster.org.rpc;

import java.util.Map;

/**
 * Created by fangyanpeng1 on 2017/7/28.
 */
public interface Request {

    int getOpaque();

    String getInterfaceName();

    String getMethodName();

    String getParameterDesc();

    Object[] getParameters();

    boolean isNeedCompress();

    Map<String, String> getAttachments();


    void setAttachment(String name, String value);
}
