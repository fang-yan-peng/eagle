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
package eagle.jfaster.org.util;

import java.util.List;

import eagle.jfaster.org.interceptor.ExecutionInterceptor;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Request;

/**
 * Created by fangyanpeng on 2018/3/31.
 */
public class InterceptorUtil {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(InterceptorUtil.class);


    public static void onBefore(Request request, List<ExecutionInterceptor> interceptors) {
        if (CollectionUtil.isEmpty(interceptors)) {
            return;
        }
        for (ExecutionInterceptor interceptor : interceptors) {
            try {
                interceptor.onBefore(request.getInterfaceName(), request.getMethodName(),
                        request.getParameters());
            } catch (Throwable e) {
                logger.error("execute interceptor.onAfter", e);
            }
        }
    }

    public static void onAfter(Request request, List<ExecutionInterceptor> interceptors) {
        if (CollectionUtil.isEmpty(interceptors)) {
            return;
        }
        for (ExecutionInterceptor interceptor : interceptors) {
            try {
                interceptor.onAfter(request.getInterfaceName(), request.getMethodName(),
                        request.getParameters());
            } catch (Throwable e) {
                logger.error("execute interceptor.onAfter", e);
            }
        }
    }

    public static void onError(Request request, List<ExecutionInterceptor> interceptors, Throwable ex) {
        if (CollectionUtil.isEmpty(interceptors)) {
            return;
        }
        for (ExecutionInterceptor interceptor : interceptors) {
            try {
                interceptor.onError(request.getInterfaceName(), request.getMethodName(), request
                        .getParameters(), ex);
            } catch (Throwable e) {
                logger.error("execute interceptor.onError", e);
            }
        }
    }
}
