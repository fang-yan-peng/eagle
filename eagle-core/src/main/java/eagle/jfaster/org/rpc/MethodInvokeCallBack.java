package eagle.jfaster.org.rpc;

/**
 * 异步函数回调
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
public interface MethodInvokeCallBack<T> {

    void onSuccess(T response);

    void onFail(Exception e);
}
