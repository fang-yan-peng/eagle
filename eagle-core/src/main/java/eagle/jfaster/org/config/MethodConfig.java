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
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class MethodConfig extends AbstractConfig {

    @Setter
    // 方法名
    private String name;
    // 超时时间
    @Getter
    @Setter
    private Integer requestTimeout;
    // 失败重试次数（默认为0，不重试）

    @Getter
    @Setter
    private Integer retries;
    // 最大并发调用

    @Getter
    @Setter
    private Integer actives;
    // 参数类型（逗号分隔）

    @Setter
    private String argumentTypes;

    @ConfigDesc(excluded = true)
    public String getName() {
        return name;
    }

    @ConfigDesc(excluded = true)
    public String getArgumentTypes() {
        return argumentTypes;
    }
}
