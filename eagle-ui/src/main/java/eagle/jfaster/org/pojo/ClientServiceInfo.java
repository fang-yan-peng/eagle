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

package eagle.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng on 2017/8/25.
 */
public class ClientServiceInfo implements ServiceCommonSetter {

    @Setter
    @Getter
    String protocol ;

    @Setter
    @Getter
    String serialization ;

    @Setter
    @Getter
    String group ;

    @Setter
    @Getter
    String serviceName ;

    @Setter
    @Getter
    String host ;

    @Setter
    @Getter
    Integer port;

    @Setter
    @Getter
    Integer process;

    @Setter
    @Getter
    String codec ;

    @Setter
    @Getter
    Integer actives ;

    @Setter
    @Getter
    Integer maxContentLength ;

    @Setter
    @Getter
    Long activesWait ;

    @Setter
    @Getter
    Boolean check ;

    @Setter
    @Getter
    Boolean useNative ;

    @Setter
    @Getter
    Boolean compress ;

    @Setter
    @Getter
    Boolean useDefault ;

    @Setter
    @Getter
    String cluster ;

    @Setter
    @Getter
    String heartbeatFactory ;

    @Setter
    @Getter
    Integer heartbeat ;

    @Setter
    @Getter
    String version ;

    @Setter
    @Getter
    String statsLog ;

    @Setter
    @Getter
    Integer retries ;

    @Setter
    @Getter
    Integer callbackThread ;

    @Setter
    @Getter
    Integer callbackQueueSize ;

    @Setter
    @Getter
    Integer callbackWaitTime ;

    @Setter
    @Getter
    Integer requestTimeout ;

    @Setter
    @Getter
    Integer connectTimeout ;

    @Setter
    @Getter
    Long idleTime ;

    @Setter
    @Getter
    Integer minClientConnection ;

    @Setter
    @Getter
    Integer maxClientConnection;

    @Setter
    @Getter
    Integer maxInvokeError ;

    @Setter
    @Getter
    String loadbalance ;

    @Setter
    @Getter
    String haStrategy ;

    @Setter
    @Getter
    Long maxLifetime ;

    @Setter
    @Getter
    String callback;

    @Setter
    @Getter
    String mock;


}
