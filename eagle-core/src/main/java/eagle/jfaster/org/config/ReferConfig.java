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

package eagle.jfaster.org.config;

import com.google.common.base.Strings;

import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.cluster.cluster.ReferClusterManage;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.interceptor.ExecutionInterceptor;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.MethodInvokeCallBack;
import eagle.jfaster.org.rpc.Mock;
import eagle.jfaster.org.rpc.RpcHandler;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.util.CollectionUtil;
import eagle.jfaster.org.util.ConfigUtil;
import eagle.jfaster.org.util.NetUtil;
import lombok.Getter;
import lombok.Setter;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static eagle.jfaster.org.constant.EagleConstants.RPC_HANDLER;
import static eagle.jfaster.org.util.PidUtil.getPid;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class ReferConfig<T> extends BaseReferConfig {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ReferConfig.class);


    private Class<T> interfaceClass;

    // 具体到方法的配置
    @Setter
    @Getter
    protected List<MethodConfig> methods;

    @Setter
    @Getter
    private BaseReferConfig baseRefer;

    @Setter
    @Getter
    protected MethodInvokeCallBack invokeCallback;

    @Setter
    @Getter
    protected String callback;

    @Setter
    @Getter
    protected Mock failMock;

    @Setter
    @Getter
    private List<ReferClusterManage<T>> clusterManages;

    private T ref;

    private AtomicBoolean stat = new AtomicBoolean(false);

    public void setInterface(Class<T> interfaceClass) {
        if (interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        this.interfaceClass = interfaceClass;
    }

    public Class<?> getInterface() {
        return interfaceClass;
    }

    public T getRef() throws Exception {
        try {
            if (ref == null) {
                initRef();
            }
            return ref;
        } catch (Exception e) {
            stat.set(false);
            logger.error(String.format("%s getRef error ", interfaceClass.getName()), e);
            throw e;
        }
    }

    public void initRef() throws Exception {
        if (stat.compareAndSet(false, true)) {
            if (CollectionUtil.isEmpty(protocols)) {
                throw new EagleFrameException("%s RefererConfig is malformed, for protocol not set correctly!", interfaceClass.getName());
            }
            ConfigUtil.checkInterfaceAndMethods(interfaceClass, methods);
            clusterManages = new ArrayList<>(protocols.size());
            //检查注册中心
            List<MergeConfig> regConfigs = ConfigUtil.loadRegistryConfigs(getRegistries());
            if (CollectionUtil.isEmpty(regConfigs)) {
                throw new IllegalStateException("Should set registry config for service:" + interfaceClass.getName());
            }
            if (Strings.isNullOrEmpty(host) && baseRefer != null) {
                host = baseRefer.getHost();
            }
            if (NetUtil.isInvalidLocalHost(host)) {
                host = ConfigUtil.getLocalHostAddress(regConfigs);
            }
            List<ReferCluster<T>> clusters = new ArrayList<ReferCluster<T>>(protocols.size());
            RpcHandler rpcHandler = SpiClassLoader.getClassLoader(RpcHandler.class).getExtension(RPC_HANDLER);
            for (ProtocolConfig protocol : protocols) {
                String protocolName = protocol.getName();
                if (Strings.isNullOrEmpty(protocolName)) {
                    protocolName = ConfigEnum.protocol.getValue();
                }
                MergeConfig referConfig = new MergeConfig();
                referConfig.setHost(host);
                referConfig.setPort(getPid());
                referConfig.setInterfaceName(interfaceClass.getName());
                referConfig.setProtocol(protocolName);
                referConfig.setVersion(Strings.isNullOrEmpty(version) ? ConfigEnum.version.getValue() : version);
                referConfig.addExt(ConfigEnum.refreshTimestamp.getName(), String.valueOf(System.currentTimeMillis()));
                referConfig.setInvokeCallBack(getInvokeCallback());
                referConfig.setMock(getFailMock());
                referConfig.setInterceptors(determinInterceptors());
                ConfigUtil.collectConfigParams(referConfig, protocol, baseRefer, this);
                ConfigUtil.collectMethodConfigParams(referConfig, this.getMethods());
                ReferClusterManage<T> clusterManage = rpcHandler.buildClusterManage(interfaceClass, referConfig, regConfigs);
                clusterManages.add(clusterManage);
                clusters.add(clusterManage.getCluster());
            }
            ref = rpcHandler.refer(interfaceClass, clusters);
        }
    }

    private List<ExecutionInterceptor> determinInterceptors() {
        if (!CollectionUtil.isEmpty(this.interceptors)) {
            return this.interceptors;
        }
        if (baseRefer != null) {
            return baseRefer.getInterceptors();
        }
        return null;
    }

    public void unRef() {
        if (stat.compareAndSet(true, false)) {
            try {
                RpcHandler rpcHandler = SpiClassLoader.getClassLoader(RpcHandler.class).getExtension(RPC_HANDLER);
                rpcHandler.unRef(clusterManages);
            } catch (Exception e) {
                logger.warn(String.format("%s close error ", interfaceClass.getName()), e);
            }
        }
    }

}
