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

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.registry.factory.RegistryCenterManage;
import eagle.jfaster.org.spi.SpiClassLoader;

import java.io.IOException;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/6.
 */
public class RegistryUtil {

    public static void closeRegistrys(List<MergeConfig> registryConfigs) throws IOException {
        if (CollectionUtil.isEmpty(registryConfigs)) {
            return;
        }
        RegistryCenterManage registryManage;
        for (MergeConfig regConfig : registryConfigs) {
            registryManage = SpiClassLoader.getClassLoader(RegistryCenterManage.class).getExtension(regConfig.getProtocol());
            registryManage.getRegistry(regConfig).close();

        }
    }
}
