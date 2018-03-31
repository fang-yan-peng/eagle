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

import java.lang.reflect.Method;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.util.ReflectUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 远端调用cglib实现
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
public class EagleRpcCglibRemoteInvoke<T> extends AbstractRemoteInvoke<T, MethodProxy> {

    public EagleRpcCglibRemoteInvoke(Class<T> interfaceClz, T invokeImpl, MergeConfig config) {
        super(interfaceClz, invokeImpl, config);
    }

    @Override
    protected void init() {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new EagleMethodInterceptor(methodInvoke));
        enhancer.setInterfaces(new Class[]{interfaceClz});
        initMethodInvoke((T) enhancer.create());
    }

    @Override
    protected Object invoke(Request request, MethodProxy method) throws Throwable {
        return method.invoke(invokeImpl, request.getParameters());
    }

    private void initMethodInvoke(T proxy) {
        try {
            Method[] methods = interfaceClz.getMethods();
            for (Method method : methods) {
                method.invoke(proxy, ReflectUtil.getParameterDefaultVals(method));
            }
        } catch (Exception e) {
            throw new EagleFrameException(e);
        }
    }
}
