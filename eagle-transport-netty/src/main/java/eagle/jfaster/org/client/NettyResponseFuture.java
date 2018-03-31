/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package eagle.jfaster.org.client;

import static eagle.jfaster.org.util.InterceptorUtil.onAfter;
import static eagle.jfaster.org.util.InterceptorUtil.onError;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import eagle.jfaster.org.interceptor.ExecutionInterceptor;
import eagle.jfaster.org.interceptor.context.CurrentExecutionContext;
import eagle.jfaster.org.rpc.MethodInvokeCallBack;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.ResponseFuture;
import eagle.jfaster.org.rpc.support.TraceContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * netty 异步处理future
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
@RequiredArgsConstructor
public class NettyResponseFuture<T> implements ResponseFuture<T> {

    //请求唯一标识
    @Getter
    private final int opaque;

    //超时时间
    @Getter
    private final long timeoutMillis;

    //异步回调
    @Getter
    private final MethodInvokeCallBack<T> callBack;

    @Getter
    private final Request request;

    @Getter
    private final CurrentExecutionContext executionContext = CurrentExecutionContext.getContext();

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
    public void executeCallback(List<ExecutionInterceptor> interceptors) {
        if (callBack != null && executeCallBackOnlyOnce.compareAndSet(false, true)) {
            CurrentExecutionContext.setContext(executionContext);
            Map<String, String> attachments = request.getAttachments();
            if (attachments != null) {
                TraceContext.setTraceId(attachments.get(TraceContext.TRACE_KEY));
            }
            try {
                if (exception != null) {
                    onError(request, interceptors, exception);
                    callBack.onFail(exception);
                } else {
                    onAfter(request, interceptors);
                    callBack.onSuccess(value);
                }
            } finally {
                CurrentExecutionContext.clean();
                TraceContext.clear();
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
        if (exception != null) {
            throw exception;
        }
        return value;
    }

    @Override
    public void onSuccess(T value) {
        this.value = value;
        this.waiter.countDown();
    }

    @Override
    public void onFail(Exception exception) {
        this.exception = exception;
        this.waiter.countDown();
    }
}
