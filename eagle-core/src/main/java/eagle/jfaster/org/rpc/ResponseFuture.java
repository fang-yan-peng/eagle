package eagle.jfaster.org.rpc;
/**
 * 由于异步操作，所以不能立刻得到返回结果
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
public interface ResponseFuture <T> {

    public void executeCallback() ;

    public boolean isTimeout() ;

    public T getValue(long timeout) throws Exception ;

    public void onSuccess(T value);

    public void onFail(Exception exception);
}
