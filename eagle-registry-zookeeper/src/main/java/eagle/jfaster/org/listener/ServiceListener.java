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

package eagle.jfaster.org.listener;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.CoordinatorRegistryCenter;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * 监听注册的service变化
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@RequiredArgsConstructor
public class ServiceListener extends AbstractChildrenDataListener {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ServiceListener.class);

    //zk地址配置
    private final MergeConfig registryConfig;

    //节点变化通知
    private final ServiceChangeListener changeListener;

    //注册中心
    private final CoordinatorRegistryCenter registryCenter;

    //refer监听地址
    private final String servicePath;

    @Override
    protected void dataChanged(String path, PathChildrenCacheEvent.Type eventType, String data) {
        if(eventType == PathChildrenCacheEvent.Type.CHILD_ADDED || eventType == PathChildrenCacheEvent.Type.CHILD_REMOVED || eventType == PathChildrenCacheEvent.Type.CHILD_UPDATED){
            try {
                PathUtil.rebalance(registryCenter,registryConfig,changeListener,servicePath);
            } catch (Exception e) {
                logger.error("Zookeeper service listener failed ",e);
            }
        }
    }
}
