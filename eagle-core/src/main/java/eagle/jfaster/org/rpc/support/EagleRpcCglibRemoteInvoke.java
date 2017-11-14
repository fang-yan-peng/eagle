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

import com.google.common.collect.Maps;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.util.ExceptionUtil;
import eagle.jfaster.org.util.ReflectUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 远端调用实现
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
public class EagleRpcCglibRemoteInvoke<T> implements RemoteInvoke<T> {

    private InternalLogger logger = InternalLoggerFactory.getInstance(EagleRpcCglibRemoteInvoke.class);

    private final Class<T> interfaceClz;

    private final T invokeImpl;

    private final MergeConfig config;

    private final Map<String,MethodProxy> methodInvoke = Maps.newHashMap();

    public EagleRpcCglibRemoteInvoke(Class<T> interfaceClz, T invokeImpl, MergeConfig config) {
        this.interfaceClz = interfaceClz;
        this.invokeImpl = invokeImpl;
        this.config = config;
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new EagleMethodInterceptor(methodInvoke));
        enhancer.setInterfaces(new Class[]{interfaceClz});
        initMethodInvoke((T) enhancer.create());
    }

    @Override
    public Response invoke(Request request) {
        String methodDesc = ReflectUtil.getMethodDesc(request.getMethodName(), request.getParameterDesc());
        MethodProxy methodProxy = methodInvoke.get(methodDesc);
        EagleResponse response = new EagleResponse();
        if(methodProxy == null){
            response.setException(new EagleFrameException("Error - invoke method '%s' is not exist",methodDesc));
            return response;
        }
        try {
            Object value = methodProxy.invoke(invokeImpl,request.getParameters());
            response.setValue(value);
        } catch (Throwable e) {
            logger.error("EagleRpcJdkRemoteInvoke invoke error",e);
            response.setException(new EagleFrameException(ExceptionUtil.transform(e)));
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

    private void initMethodInvoke(T proxy) {
        try {
            Method[] methods = interfaceClz.getMethods();
            for (Method method : methods) {
                method.invoke(proxy,ReflectUtil.getParameterDefaultVals(method));
            }
        } catch (Exception e) {
            throw new EagleFrameException(e);
        }
    }
}
