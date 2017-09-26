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

import eagle.jfaster.org.config.annotation.ConfigDesc;
import lombok.Getter;
import lombok.Setter;

/**
 * 暴露和订阅协议配置
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class ProtocolConfig extends AbstractConfig {

    // 服务协议
    @Setter
    private String name;

    // 序列化方式
    @Setter
    @Getter
    private String serialization;

    // 协议编码
    @Setter
    @Getter
    private String codec;

    // IO线程池大小
    @Setter
    @Getter
    private Integer selectThreadSize;

    // 最小工作pool线程数
    @Setter
    @Getter
    protected Integer coreWorkerThread;

    // 最大工作pool线程数
    @Setter
    @Getter
    protected Integer maxWorkerThread;

    // 请求响应包的最大长度限制
    @Setter
    @Getter
    protected Integer maxContentLength;

    // server支持的最大连接数
    @Setter
    @Getter
    protected Integer maxServerConnection;

    // 是否延迟init
    @Setter
    @Getter
    protected Boolean lazyInit;

    @Setter
    @Getter
    protected Boolean useNative;

    // 采用哪种cluster 的实现
    @Setter
    @Getter
    protected String cluster;

    // 线程池队列大小
    @Setter
    @Getter
    protected Integer workerQueueSize;

    // proxy type, like jdk or javassist
    @Setter
    @Getter
    protected String proxy;

    // filter, 多个filter用","分割，blank string 表示采用默认的filter配置
    @Setter
    @Getter
    protected String filter;

    // 是否缺省配置
    @Setter
    @Getter
    private Boolean useDefault;

    @Setter
    @Getter
    private String heartbeatFactory;

    @Setter
    @Getter
    private Integer heartbeat;

    @Setter
    @Getter
    private String protectStrategy;

    @ConfigDesc(excluded = true)
    public String getName() {
        return name;
    }
}
