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

package eagle.jfaster.org.factory;

import com.google.common.base.Optional;

import eagle.jfaster.org.service.InterfaceApiService;
import eagle.jfaster.org.service.impl.InterfaceApiServiceImpl;

/**
 * @author fangyanpeng
 */
public class ServiceApiFactory {

    /**
     * 创建服务管理API对象.
     *
     * @param connectString 注册中心连接字符串
     * @param namespace 注册中心命名空间
     * @param digest 注册中心凭证
     * @return 服务管理API对象
     */
    public static InterfaceApiService createServiceAPI(final String connectString, final String namespace, final Optional<String> digest) {
        return new InterfaceApiServiceImpl(RegistryCenterFactory.createCoordinatorRegistryCenter(connectString, namespace, digest));
    }
}
