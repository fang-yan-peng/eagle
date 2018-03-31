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

package eagle.jfaster.org.factory;

import eagle.jfaster.org.CoordinatorRegistryCenter;
import eagle.jfaster.org.ZookeeperRegistryCenter;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.listener.RefListener;
import eagle.jfaster.org.listener.ServiceListener;
import eagle.jfaster.org.listener.ZkConnectionStatListener;
import eagle.jfaster.org.registry.RegistryCenter;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.registry.factory.support.AbstractRegistryManage;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.util.CollectionUtil;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "zookeeper")
public class ZookeeperRegistryCenterManage extends AbstractRegistryManage {

    /**
     * 暴露服务的路径
     * zk的路径: /interface/protocol/service/ip:port
     */
    public static final String SERVICE = "/%s/%s/service/%s";

    public static final String SERVICE_CHILDREN = "/%s/%s/service";

    /**
     * 订阅服务的路径
     * zk的路径: /interface/protocol/ref/ip:port
     */
    public static final String REF = "/%s/%s/ref/%s";

    public static final String REF_CHILDREN = "/%s/%s/ref";

    @Override
    protected RegistryCenter createRegistry(MergeConfig registryConfig) {
        RegistryCenter registryCenter = new ZookeeperRegistryCenter(registryConfig);
        registryCenter.init();
        return registryCenter;
    }

    @Override
    public void registerService(MergeConfig regConfig, MergeConfig serviceConfig) {
        registerCommon((CoordinatorRegistryCenter) this.getRegistry(regConfig), serviceConfig, SERVICE);

    }

    @Override
    public void registerRef(MergeConfig regConfig, MergeConfig refConfig) {
        registerCommon((CoordinatorRegistryCenter) this.getRegistry(regConfig), refConfig, REF);

    }

    @Override
    public void addServiceListener(MergeConfig regConfig, MergeConfig refConfig, ServiceChangeListener listener) {
        CoordinatorRegistryCenter registry = (CoordinatorRegistryCenter) this.getRegistry(regConfig);
        ServiceListener serviceListener = new ServiceListener(regConfig, listener, registry, String.format(SERVICE_CHILDREN, refConfig.getInterfaceName(), refConfig.getProtocol()));
        addListenerCommon(registry, refConfig, SERVICE_CHILDREN, serviceListener, false);
    }

    @Override
    public void addConnectionStatListener(MergeConfig regConfig, MergeConfig refConfig, ServiceChangeListener listener) {
        CoordinatorRegistryCenter registry = (CoordinatorRegistryCenter) this.getRegistry(regConfig);
        ZkConnectionStatListener statListener = new ZkConnectionStatListener(regConfig, listener, registry, String.format(SERVICE_CHILDREN, refConfig.getInterfaceName(), refConfig.getProtocol()));
        CuratorFramework client = (CuratorFramework) registry.getRawClient();
        client.getConnectionStateListenable().addListener(statListener);
    }

    @Override
    public void addRefListener(MergeConfig regConfig, MergeConfig refConfig, ServiceChangeListener listener) {
        RefListener refListener = new RefListener(regConfig, refConfig.hostPort(), listener);
        addListenerCommon((CoordinatorRegistryCenter) this.getRegistry(regConfig), refConfig, REF_CHILDREN, refListener, true);
    }

    @Override
    public List<MergeConfig> getRegisterServices(MergeConfig regConfig, MergeConfig refConfig) {
        return getServiceCommon((CoordinatorRegistryCenter) this.getRegistry(regConfig), refConfig, SERVICE_CHILDREN, SERVICE);
    }

    @Override
    public List<MergeConfig> getSubscribeServices(MergeConfig regConfig, MergeConfig refConfig) {
        return getServiceCommon((CoordinatorRegistryCenter) this.getRegistry(regConfig), refConfig, REF_CHILDREN, REF);
    }

    public void registerCommon(CoordinatorRegistryCenter registryCenter, MergeConfig config, String format) {
        String protocol = config.getProtocol();
        String interfaceName = config.getInterfaceName();
        String host = config.hostPort();
        String path = String.format(format, interfaceName, protocol, host);
        registryCenter.persistEphemeral(path, config.encode());
    }

    public void addListenerCommon(CoordinatorRegistryCenter registryCenter, MergeConfig config, String format, PathChildrenCacheListener listener, boolean cacheData) {
        String protocol = config.getProtocol();
        String interfaceName = config.getInterfaceName();
        String path = String.format(format, interfaceName, protocol);
        registryCenter.addChildrenCacheData(path, cacheData).getListenable().addListener(listener);
    }

    public List<MergeConfig> getServiceCommon(CoordinatorRegistryCenter registryCenter, MergeConfig config, String pathFormat, String dataFormat) {
        String protocol = config.getProtocol();
        String interfaceName = config.getInterfaceName();
        String path = String.format(pathFormat, interfaceName, protocol);
        List<String> hosts = registryCenter.getChildrenKeys(path);
        if (CollectionUtil.isEmpty(hosts)) {
            return null;
        }
        List<MergeConfig> configs = new ArrayList<>(hosts.size());
        for (String host : hosts) {
            String data = registryCenter.getDirectly(String.format(dataFormat, interfaceName, protocol, host));
            configs.add(MergeConfig.decode(data));
        }
        return configs;
    }
}
