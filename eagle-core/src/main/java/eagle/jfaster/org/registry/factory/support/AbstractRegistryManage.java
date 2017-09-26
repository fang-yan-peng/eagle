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

package eagle.jfaster.org.registry.factory.support;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.registry.RegistryCenter;
import eagle.jfaster.org.registry.factory.RegistryCenterManage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
public abstract class AbstractRegistryManage implements RegistryCenterManage {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(AbstractRegistryManage.class);


    private static ConcurrentHashMap<String, RegistryCenter> registries = new ConcurrentHashMap<>();

    private ReadWriteLock rdwrLock = new ReentrantReadWriteLock();

    @Override
    public RegistryCenter getRegistry(MergeConfig registryConfig) {
        String registryKey = registryConfig.identity();
        RegistryCenter center = null;
        try {
            rdwrLock.readLock().lock();
            center = registries.get(registryKey);
            if(center == null){
                try {
                    rdwrLock.readLock().unlock();
                    rdwrLock.writeLock().lock();
                    center = registries.get(registryKey);
                    if(center == null){
                        center = createRegistry(registryConfig);
                        registries.put(registryKey,center);
                    }
                } finally {
                    rdwrLock.readLock().lock();
                    rdwrLock.writeLock().unlock();
                }
            }
        } catch (Exception e) {
            logger.error("Error getRegistry ",e);
            throw new EagleFrameException(e);
        } finally {
            rdwrLock.readLock().unlock();
        }
        return center;
    }

    protected abstract RegistryCenter createRegistry(MergeConfig registryConfig);
}
