package eagle.jfaster.org.exception;

import lombok.NoArgsConstructor;

/**
 * 统一异常定义
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */
@NoArgsConstructor
public class EagleFrameException extends RuntimeException {

    public EagleFrameException(String format,Object... args){
        super(String.format(format,args));
    }

    public EagleFrameException(Throwable ex){
        super(ex);
    }
}
