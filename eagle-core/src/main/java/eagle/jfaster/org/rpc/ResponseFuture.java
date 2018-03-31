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

package eagle.jfaster.org.rpc;

import java.util.List;

import eagle.jfaster.org.interceptor.ExecutionInterceptor;

/**
 * 由于异步操作，所以不能立刻得到返回结果
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
public interface ResponseFuture<T> {

    public void executeCallback(List<ExecutionInterceptor> interceptors);

    public boolean isTimeout();

    public T getValue(long timeout) throws Exception;

    public void onSuccess(T value);

    public void onFail(Exception exception);
}
