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

import com.google.common.base.Strings;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * Created by fangyanpeng1 on 2017/8/6.
 */
@RequiredArgsConstructor
public class RefListener extends AbstractChildrenDataListener {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(RefListener.class);

    //zk地址配置
    private final MergeConfig registryConfig;

    //
    private final String refHost;

    //节点变化通知
    private final ServiceChangeListener changeListener;

    @Override
    protected void dataChanged(String path, PathChildrenCacheEvent.Type eventType, String data) {
        if(eventType == PathChildrenCacheEvent.Type.CHILD_UPDATED){
            try {
                String host = PathUtil.getHostByPath(path);
                if(Strings.isNullOrEmpty(host) || !host.equals(refHost) || Strings.isNullOrEmpty(data)){
                    return;
                }
                this.changeListener.refChange(registryConfig, MergeConfig.decode(data));
            } catch (Exception e) {
                logger.error("Zookeeper service listener failed ",e);
            }
        }
    }
}
