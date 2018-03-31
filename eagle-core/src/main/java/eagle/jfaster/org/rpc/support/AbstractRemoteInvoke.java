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
package eagle.jfaster.org.rpc.support;

import static eagle.jfaster.org.util.InterceptorUtil.onAfter;
import static eagle.jfaster.org.util.InterceptorUtil.onBefore;
import static eagle.jfaster.org.util.InterceptorUtil.onError;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.interceptor.ExecutionInterceptor;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.util.ReflectUtil;

/**
 * Created by fangyanpeng1 on 2018/3/31.
 */
public abstract class AbstractRemoteInvoke<T, M> implements RemoteInvoke<T> {

    private InternalLogger logger = InternalLoggerFactory.getInstance(AbstractRemoteInvoke.class);

    protected final Class<T> interfaceClz;

    protected final T invokeImpl;

    protected final MergeConfig config;

    protected final List<ExecutionInterceptor> interceptors;

    protected final Map<String, M> methodInvoke = Maps.newHashMap();

    public AbstractRemoteInvoke(Class<T> interfaceClz, T invokeImpl, MergeConfig config) {
        this.interfaceClz = interfaceClz;
        this.invokeImpl = invokeImpl;
        this.config = config;
        this.interceptors = config.getInterceptors();
        this.init();
    }

    @Override
    public Response invoke(Request request) {
        onBefore(request, interceptors);
        String methodDesc = ReflectUtil.getMethodDesc(request.getMethodName(), request.getParameterDesc());
        EagleResponse response = new EagleResponse();
        M method = methodInvoke.get(methodDesc);
        if (method == null) {
            Exception ex = new EagleFrameException("Error - invoke method '%s' is not exist", methodDesc);
            response.setException(ex);
            onError(request, interceptors, ex);
            return response;
        }
        try {
            Object value = this.invoke(request, method);
            response.setValue(value);
            onAfter(request, interceptors);
        } catch (Throwable e) {
            logger.error(String.format("%s AbstractRemoteInvoke invoke error", request.getOpaque()), e);
            response.setException(new EagleFrameException(e.getMessage()));
            onError(request, interceptors, e);
        }
        return response;
    }

    @Override
    public MergeConfig getConfig() {
        return config;
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClz;
    }

    protected abstract void init();

    protected abstract Object invoke(Request request, M method) throws Throwable;
}
