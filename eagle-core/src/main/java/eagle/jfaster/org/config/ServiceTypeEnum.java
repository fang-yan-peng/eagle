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

package eagle.jfaster.org.config;

import com.google.common.base.Strings;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.support.EagleRpcCglibRemoteInvoke;
import eagle.jfaster.org.rpc.support.EagleRpcJdkRemoteInvoke;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fangyanpeng on 2017/11/13.
 */

public enum ServiceTypeEnum {

    JDK("jdk", EagleRpcJdkRemoteInvoke.class), CGLIB("cglib", EagleRpcCglibRemoteInvoke.class);

    ServiceTypeEnum(String type, Class<?> invokeClass) {
        this.type = type;
        this.invokeClass = invokeClass;
    }

    @Getter
    private String type;

    @Getter
    private Class<?> invokeClass;

    public static ServiceTypeEnum typeOf(String type) {
        if (Strings.isNullOrEmpty(type)) {
            return JDK;
        }
        for (ServiceTypeEnum typeEnum : ServiceTypeEnum.values()) {
            if (typeEnum.type.equalsIgnoreCase(type)) {
                return typeEnum;
            }
        }
        return JDK;
    }

    public static <T> RemoteInvoke<T> getRemoteInvoke(String type, Class<T> interfaceClass, T ref, MergeConfig serviceConfig) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ServiceTypeEnum serviceType = typeOf(type);
        Class<T> clz = (Class<T>) serviceType.getInvokeClass();
        Constructor invokeCtor = clz.getConstructor(Class.class, Object.class, MergeConfig.class);
        return (RemoteInvoke<T>) invokeCtor.newInstance(interfaceClass, ref, serviceConfig);
    }

}
