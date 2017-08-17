package eagle.jfaster.org.rpc.support;

import com.google.common.collect.Maps;
import eagle.jfaster.org.rpc.Request;
import lombok.Setter;

import java.util.Map;

/**
 * 默认请求体
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
public class EagleRequest implements Request {

    @Setter
    private int opaque;

    @Setter
    private String interfaceName;

    @Setter
    private String methodName;

    @Setter
    private String parameterDesc;

    @Setter
    private boolean needCompress = false;

    @Setter
    private Object[] parameters;

    @Setter
    private Map<String,String> attachments;

    @Override
    public int getOpaque() {
        return opaque;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getParameterDesc() {
        return parameterDesc;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public boolean isNeedCompress() {
        return needCompress;
    }

    @Override
    public Map<String, String> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachment(String name, String value) {
        if(attachments == null){
            attachments = Maps.newHashMap();
        }
        attachments.put(name,value);
    }
}
