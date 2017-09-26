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

package eagle.jfaster.org.service;

import eagle.jfaster.org.pojo.ClientServiceInfo;
import eagle.jfaster.org.pojo.ServerServiceInfo;
import eagle.jfaster.org.pojo.ServiceBriefInfo;

import java.util.List;

/**
 * Created by fangyanpeng on 2017/8/24.
 */
public interface InterfaceApiService {

    int getServicesTotalCount();

    List<ServiceBriefInfo> getClientBriefInfos();

    ClientServiceInfo getClientConfig(String serviceName, String protocol, String host);

    boolean deleteClientConfig(String serviceName, String protocol);

    boolean updateClientConfig(ClientServiceInfo serviceInfo);

    List<ServiceBriefInfo> getServerBriefInfos();

    ServerServiceInfo getServerConfig(String serviceName, String protocol, String host);

    boolean deleteServerConfig(String serviceName, String protocol);

    boolean updateServerConfig(ServerServiceInfo serviceInfo);

    boolean disableServer(String serviceName, String protocol, String host);

    boolean enableServer(String serviceName, String protocol, String host);

}
