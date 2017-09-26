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

import lombok.Getter;
import lombok.Setter;

/**
 * 注册中心配置
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class RegistryConfig extends AbstractConfig {

    @Getter
    @Setter
    // 注册配置名称
    private String name;

    @Getter
    @Setter
    // 注册协议
    private String protocol;

    @Getter
    @Setter
    // 注册中心地址，支持多个ip+port，格式：ip1:port1,ip2:port2,ip3，如果没有port，则使用默认的port
    private String address;

    @Getter
    @Setter
    //命名空间
    private String namespace;

    @Getter
    @Setter
    private Integer baseSleepTimeMilliseconds;

    @Getter
    @Setter
    private Integer maxSleepTimeMilliseconds;

    @Getter
    @Setter
    private Integer maxRetries;

    @Getter
    @Setter
    private Integer sessionTimeoutMilliseconds;

    @Getter
    @Setter
    // 注册中心连接超时时间(毫秒)
    private Integer connectionTimeoutMilliseconds;

    @Getter
    @Setter
    // 在该注册中心上服务是否暴露
    private Boolean register;

    @Getter
    @Setter
    // 在该注册中心上服务是否引用
    private Boolean subscribe;
}
