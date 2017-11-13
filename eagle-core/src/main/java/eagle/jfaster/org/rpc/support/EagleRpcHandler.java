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

package eagle.jfaster.org.rpc.support;

import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.cluster.cluster.ReferClusterManage;
import eagle.jfaster.org.cluster.proxy.AbstractReferInvokeHandler;
import eagle.jfaster.org.cluster.proxy.AsyncInvokeHandler;
import eagle.jfaster.org.cluster.proxy.SyncInvokeHandler;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.ServiceTypeEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.protocol.Protocol;
import eagle.jfaster.org.registry.factory.RegistryCenterManage;
import eagle.jfaster.org.rpc.Exporter;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.RpcHandler;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.util.RegistryUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/6.
 */
@SpiInfo(name = "eagle")
public class EagleRpcHandler implements RpcHandler {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(EagleRpcHandler.class);


    @Override
    public <T> ReferClusterManage<T> buildClusterManage(Class<T> interfaceClass, MergeConfig refConfig, List<MergeConfig> registryConfigs) {
        ReferClusterManage<T> clusterManage = new ReferClusterManage<T>(interfaceClass,refConfig,registryConfigs);
        clusterManage.init();
        return clusterManage;
    }

    @Override
    public <T> T refer(Class<T> interfaceClass, List<ReferCluster<T>> clusters) {
        AbstractReferInvokeHandler<T> invokeHandler;
        if(clusters.get(0).getConfig().getInvokeCallBack() == null){
            invokeHandler = new SyncInvokeHandler<T>(clusters,interfaceClass);
        }else {
            invokeHandler = new AsyncInvokeHandler<T>(clusters,interfaceClass);
        }
        return (T) Proxy.newProxyInstance(EagleRpcHandler.class.getClassLoader(),new Class[]{interfaceClass},invokeHandler);
    }

    @Override
    public <T> Exporter<T> export(Class<T> interfaceClass, T ref, MergeConfig serviceConfig, List<MergeConfig> registryConfigs) {
        try {
            String protoName = serviceConfig.getProtocol();
            Protocol<T> protocol = SpiClassLoader.getClassLoader(Protocol.class).getExtension(protoName);
            String serviceType = serviceConfig.getExt(ConfigEnum.serviceType.getName(),ConfigEnum.serviceType.getValue());
            //RemoteInvoke<T> invoke = ServiceTypeEnum.typeOf(serviceType) == ServiceTypeEnum.CGLIB ? new EagleRpcCglibRemoteInvoke<T>(interfaceClass,ref,serviceConfig) : new EagleRpcJdkRemoteInvoke<T>(interfaceClass,ref,serviceConfig);
            RemoteInvoke<T> invoke = ServiceTypeEnum.getRemoteInvoke(serviceType,interfaceClass,ref,serviceConfig);
            Exporter<T> exporter = protocol.createServer(invoke);
            RegistryCenterManage registryManage;
            for(MergeConfig regConfig : registryConfigs){
                registryManage = SpiClassLoader.getClassLoader(RegistryCenterManage.class).getExtension(regConfig.getProtocol());
                registryManage.registerService(regConfig,serviceConfig);
            }
            return exporter;
        } catch (Exception e) {
            throw new EagleFrameException(e);
        }
    }

    @Override
    public <T> void unexport(List<Exporter<T>> exporters, List<MergeConfig> registryConfigs) {
        try {
            RegistryUtil.closeRegistrys(registryConfigs);
            for(Exporter exporter : exporters){
                exporter.close();
            }
        } catch (IOException e) {
            logger.error("EagleRpcHandler.unexport exception",e);
        }
    }

    @Override
    public <T> void unRef(List<ReferClusterManage<T>> clusterManages) {
        for (ReferClusterManage manage : clusterManages){
            manage.destroy();
        }
    }
}
