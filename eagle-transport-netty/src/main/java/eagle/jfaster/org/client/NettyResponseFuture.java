package eagle.jfaster.org.client;

import eagle.jfaster.org.rpc.MethodInvokeCallBack;
import eagle.jfaster.org.rpc.ResponseFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * netty 异步处理future
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
@RequiredArgsConstructor
public class NettyResponseFuture<T> implements ResponseFuture <T> {

    //请求唯一标识
    @Getter
    private final int opaque;

    //超时时间
    @Getter
    private final long timeoutMillis;

    //异步回调
    @Getter
    private final MethodInvokeCallBack<T> callBack;

    //请求开始时间
    @Getter
    private final long beginTimestamp = System.currentTimeMillis();

    //线程等待
    private final CountDownLatch waiter = new CountDownLatch(1);

    //发送是否成功
    @Setter
    @Getter
    private volatile boolean sendRequestOK = true;

    //正常结果
    @Setter
    private volatile T value;

    //异常
    @Setter
    @Getter
    private volatile Exception exception;

    //由于超时和正常回调有可能同时执行，要确保回调只执行一次
    private AtomicBoolean executeCallBackOnlyOnce = new AtomicBoolean(false);

    @Override
    public void executeCallback() {
        if (callBack != null && executeCallBackOnlyOnce.compareAndSet(false,true)) {
            if(exception != null){
                callBack.onFail(exception);
            }else {
                callBack.onSuccess(value);
            }
        }
    }

    @Override
    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }

    @Override
    public T getValue(long timeout) throws Exception {
        this.waiter.await(timeout, TimeUnit.MILLISECONDS);
        if(exception != null){
            throw exception;
        }
        return value;
    }

    @Override
    public void onSuccess(T value){
        this.value = value;
        this.waiter.countDown();
    }

    @Override
    public void onFail(Exception exception){
        this.exception = exception;
        this.waiter.countDown();
    }
}
