package eagle.jfaster.org.rpc.support;

import com.google.common.collect.Maps;
import eagle.jfaster.org.rpc.Response;
import lombok.Setter;

import java.util.Map;

/**
 * 默认返回体
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
public class EagleResponse implements Response {

    @Setter
    private Object value;

    @Setter
    private Exception exception;

    @Setter
    private int opaque;

    @Setter
    private boolean needCompress;

    @Setter
    private Map<String,String> attachments;

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public boolean isNeedCompress() {
        return needCompress;
    }

    @Override
    public int getOpaque() {
        return opaque;
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
