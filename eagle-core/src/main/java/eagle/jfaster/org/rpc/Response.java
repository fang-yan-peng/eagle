package eagle.jfaster.org.rpc;

import java.util.Map;

/**
 * Created by fangyanpeng1 on 2017/7/28.
 */
public interface Response {
    //正常的返回值
    Object getValue();

    //返回异常
    Exception getException();

    boolean isNeedCompress();

    //请求的唯一标识
    int getOpaque();

    //附加信息
    Map<String, String> getAttachments();

    void setAttachment(String name, String value);
}
