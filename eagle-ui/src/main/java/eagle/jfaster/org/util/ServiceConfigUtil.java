
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

import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.pojo.ServiceCommonSetter;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Created by fangyanpeng on 2017/8/25.
 */
public class ServiceConfigUtil {

    public static <T extends ServiceCommonSetter> void getServiceInfo(Class<T> targetClass, MergeConfig config, T target) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(targetClass);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            target.setVersion(config.getVersion());
            target.setServiceName(config.getInterfaceName());
            target.setProtocol(config.getProtocol());
            target.setPort(config.getPort());
            target.setProcess(config.getPort());
            target.setHost(config.getHost());
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                if (ignore(name)) {
                    continue;
                }
                Method writeM = descriptor.getWriteMethod();
                Method readM = descriptor.getReadMethod();
                ConfigEnum configEnum = ConfigEnum.valueOf(name);
                Class returnType = readM.getReturnType();
                if (returnType == String.class) {
                    writeM.invoke(target, config.getExt(configEnum.getName(), configEnum.getValue()));
                } else if (returnType == Integer.class || returnType == int.class) {
                    writeM.invoke(target, config.getExtInt(configEnum.getName(), configEnum.getIntValue()));
                } else if (returnType == Long.class || returnType == long.class) {
                    writeM.invoke(target, config.getExtLong(configEnum.getName(), configEnum.getLongValue()));
                } else if (returnType == Boolean.class || returnType == boolean.class) {
                    writeM.invoke(target, config.getExtBoolean(configEnum.getName(), configEnum.isBooleanValue()));
                }
            }
        } catch (Exception e) {
            Logs.error("getServiceInfo", e);
        }
    }

    public static <T extends ServiceCommonSetter> void setServiceInfo(Class<T> targetClass, MergeConfig config, T target) throws Exception {
        config.setProtocol(target.getProtocol());
        config.setVersion(target.getVersion());
        config.setInterfaceName(target.getServiceName());
        config.setHost(target.getHost());
        config.setPort(target.getPort());
        if (config.getPort() == null || config.getPort() == 0) {
            config.setPort(target.getProcess());
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(targetClass);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            if (ignore(name)) {
                continue;
            }
            Method readM = descriptor.getReadMethod();
            ConfigEnum configEnum = ConfigEnum.valueOf(name);
            Object val = readM.invoke(target);
            if (val != null) {
                String varStr = String.valueOf(val);
                if (!varStr.trim().isEmpty()) {
                    config.addExt(configEnum.getName(), varStr);
                }
            }
        }
    }

    private static boolean ignore(String name) {
        return ("host".equals(name) || "class".equals(name) || "process".equals(name) || "serviceName".equals(name) || "protocol".equals(name) || "version".equals(name) || "port".equals(name));
    }
}
