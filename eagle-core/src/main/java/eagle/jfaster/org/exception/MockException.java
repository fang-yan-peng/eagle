package eagle.jfaster.org.exception;

/**
 * Created by fangyanpeng on 2017/8/29.
 */
public class MockException extends RuntimeException {

    public MockException(String format,Object... args){
        super(String.format(format,args));
    }

}
