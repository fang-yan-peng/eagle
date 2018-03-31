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

package eagle.jfaster.org.interceptor;

import eagle.jfaster.org.interceptor.context.CurrentExecutionContext;

/**
 * 执行拦截器，拦截器之间可以通过 {@link CurrentExecutionContext} 传递任意参数。
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */
public interface ExecutionInterceptor {

    void onBefore(String interfaceName, String method, Object[] args);

    void onAfter(String interfaceName, String method, Object[] args);

    void onError(String interfaceName, String method, Object[] args, Throwable e);
}
