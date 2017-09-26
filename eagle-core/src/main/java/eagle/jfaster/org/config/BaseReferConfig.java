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
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class BaseReferConfig extends AbstractInterfaceConfig{

    // 请求超时
    @Setter
    @Getter
    protected Integer requestTimeout;

    // 连接超时
    @Setter
    @Getter
    protected Long connectTimeout;

    // client最小连接数
    @Setter
    @Getter
    protected Integer minClientConnection;

    // client最大连接数
    @Setter
    @Getter
    protected Integer maxClientConnection;

    @Setter
    @Getter
    protected Long idleTime;

    @Setter
    @Getter
    protected Long maxLifetime;

    @Setter
    @Getter
    protected Integer maxInvokeError;

    @Getter
    @Setter
    // 是否开启gzip压缩
    protected Boolean compress;

    @Getter
    @Setter
    // 进行gzip压缩的最小阈值，且大于此值时才进行gzip压缩。单位Byte
    protected Integer minCompressSize;

    // loadbalance 方式
    @Setter
    @Getter
    protected String loadbalance;

    // 高可用策略
    @Setter
    @Getter
    protected String haStrategy;

    //回调执行的线程数
    @Setter
    @Getter
    protected Integer callbackThread;

    //回调任务对列大小
    @Setter
    @Getter
    protected Integer callbackQueueSize;

    @Setter
    @Getter
    protected Integer callbackWaitTime;


}
