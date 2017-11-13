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

import eagle.jfaster.org.util.ReflectUtil;
import lombok.RequiredArgsConstructor;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by fangyanpeng on 2017/11/13.
 */
@RequiredArgsConstructor
public class EagleMethodInterceptor implements MethodInterceptor {

    private final Map<String,MethodProxy>  proxyMap;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodDesc = ReflectUtil.getMethodDesc(method);
        proxyMap.put(methodDesc, proxy);
        return ReflectUtil.getDefaultReturnValue(method.getReturnType());
    }
}
