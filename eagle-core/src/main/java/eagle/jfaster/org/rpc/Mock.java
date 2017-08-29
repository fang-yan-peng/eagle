package eagle.jfaster.org.rpc;

/**
 * 失败mock类
 *
 * Created by fangyanpeng on 2017/8/29.
 */
public interface Mock <T> {
    T getMockValue(String interfaceName,String methodName, Object[] parameters,Throwable e);
}
