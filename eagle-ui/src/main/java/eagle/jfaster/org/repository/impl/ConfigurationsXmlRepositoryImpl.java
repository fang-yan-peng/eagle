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

package eagle.jfaster.org.repository.impl;


import eagle.jfaster.org.pojo.GlobalConfiguration;
import eagle.jfaster.org.repository.ConfigurationsXmlRepository;

/**
 * 基于XML的全局配置数据访问器实现类.
 *
 * @author fangyanpeng
 */
public final class ConfigurationsXmlRepositoryImpl extends AbstractXmlRepositoryImpl<GlobalConfiguration> implements ConfigurationsXmlRepository {

    public ConfigurationsXmlRepositoryImpl() {
        super("Configurations.xml", GlobalConfiguration.class);
    }
}
