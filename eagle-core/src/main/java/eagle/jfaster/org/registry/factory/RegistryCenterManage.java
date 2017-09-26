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

package eagle.jfaster.org.registry.factory;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.registry.RegistryCenter;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
@Spi(scope = Scope.SINGLETON)
public interface RegistryCenterManage {

    RegistryCenter getRegistry(MergeConfig regConfig);

    void registerService(MergeConfig regConfig,MergeConfig serviceConfig);

    void registerRef(MergeConfig regConfig,MergeConfig refConfig);

    void addServiceListener(MergeConfig regConfig,MergeConfig refConfig,ServiceChangeListener listener);

    void addConnectionStatListener(MergeConfig regConfig,MergeConfig refConfig,ServiceChangeListener listener) ;

    void addRefListener(MergeConfig regConfig,MergeConfig refConfig,ServiceChangeListener listener);

    List<MergeConfig> getRegisterServices(MergeConfig regConfig,MergeConfig refConfig);

    List<MergeConfig> getSubscribeServices(MergeConfig regConfig,MergeConfig refConfig);

}
