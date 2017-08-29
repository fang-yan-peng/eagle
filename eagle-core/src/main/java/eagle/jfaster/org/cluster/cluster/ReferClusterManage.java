package eagle.jfaster.org.cluster.cluster;

import eagle.jfaster.org.cluster.HaStrategy;
import eagle.jfaster.org.cluster.LoadBalance;
import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.protocol.Protocol;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.registry.factory.RegistryCenterManage;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.util.CollectionUtil;
import eagle.jfaster.org.util.RegistryUtil;
import lombok.Getter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * refer集群管理
 *
 * Created by fangyanpeng1 on 2017/8/6.
 */
public class ReferClusterManage<T> implements ServiceChangeListener {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ReferClusterManage.class);

    @Getter
    private ReferCluster<T> cluster;

    @Getter
    private Class<T> interfaceClass;

    @Getter
    private volatile MergeConfig refConfig;

    @Getter
    private List<MergeConfig> registryConfigs;

    private Protocol protocol;

    private ConcurrentHashMap<MergeConfig, List<Refer<T>>> registryRefers = new ConcurrentHashMap<>();

    public ReferClusterManage(Class<T> interfaceClass, MergeConfig refConfig, List<MergeConfig> registryConfigs) {
        this.interfaceClass = interfaceClass;
        this.refConfig = refConfig;
        this.registryConfigs = registryConfigs;
    }

    public void init(){
        initCluster();
        initProtocol();
        registerClient();
    }

    public void initCluster(){
        String clusterName = refConfig.getExt(ConfigEnum.cluster.getName(),ConfigEnum.cluster.getValue());
        String loadbalanceName = refConfig.getExt(ConfigEnum.loadbalance.getName(),ConfigEnum.loadbalance.getValue());
        String haStrategyName = refConfig.getExt(ConfigEnum.haStrategy.getName(),ConfigEnum.haStrategy.getValue());
        cluster = SpiClassLoader.getClassLoader(ReferCluster.class).getExtension(clusterName);
        LoadBalance<T> loadBalance = SpiClassLoader.getClassLoader(LoadBalance.class).getExtension(loadbalanceName);
        HaStrategy<T> haStrategy = SpiClassLoader.getClassLoader(HaStrategy.class).getExtension(haStrategyName);
        cluster.setConfig(refConfig);
        cluster.setLoadBalance(loadBalance);
        cluster.setHaStrategy(haStrategy);
        cluster.init();
    }

    public void initProtocol(){
        protocol = SpiClassLoader.getClassLoader(Protocol.class).getExtension(refConfig.getProtocol());
    }
    public void registerClient(){
        RegistryCenterManage registryManage;
        for(MergeConfig config : registryConfigs){
            registryManage = SpiClassLoader.getClassLoader(RegistryCenterManage.class).getExtension(config.getProtocol());
            registryManage.registerRef(config,refConfig);
            registryManage.addServiceListener(config,refConfig,this);
            registryManage.addRefListener(config,refConfig,this);
            registryManage.addConnectionStatListener(config,refConfig,this);
            List<MergeConfig> configs = registryManage.getRegisterServices(config,refConfig);
            if(CollectionUtil.isEmpty(configs)){
                continue;
            }
            serviceChange(config,configs);

        }
        boolean check = refConfig.getExtBoolean(ConfigEnum.check.getName(),ConfigEnum.check.isBooleanValue());
        if(check && CollectionUtil.isEmpty(cluster.getRefers())){
            throw new EagleFrameException("%s no available services",refConfig.identity());
        }
    }

    @Override
    public synchronized void serviceChange(MergeConfig registryConfig, List<MergeConfig> serviceConfigs) {
        if(CollectionUtil.isEmpty(serviceConfigs)){
            removeRegistry(registryConfig);
            return;
        }
        logger.info(String.format("Refer %s subscribe to change,registry:%s",refConfig.identity(),registryConfig.identity()));
        List<Refer<T>> existRefers = registryRefers.get(registryConfig);
        List<Refer<T>> newRefers = new ArrayList<>();
        for(MergeConfig serviceConfig : serviceConfigs){
            if(!serviceConfig.isSupport(refConfig) || serviceConfig.disable()){
                continue;
            }

            Refer<T> refer = getExistingRefer(serviceConfig,existRefers);
            //获取权重信息
            int weight = serviceConfig.getExtInt(ConfigEnum.weight.getName(),0);
            if(weight > 0){
                refConfig.addExt(ConfigEnum.weight.getName(),String.valueOf(weight));
            }
            if(refer == null){
                serviceConfig.update(refConfig);
                serviceConfig.setInvokeCallBack(refConfig.getInvokeCallBack());
                refer = protocol.createRefer(serviceConfig,interfaceClass);
            }else {
                refer.updateConfig(refConfig);
            }
            if(refer != null){
                newRefers.add(refer);
            }
        }
        if(CollectionUtil.isEmpty(newRefers)){
            removeRegistry(registryConfig);
            return;
        }
        registryRefers.put(registryConfig,newRefers);
        refreshCluster();

    }

    @Override
    public synchronized void refChange(MergeConfig registryConfig, MergeConfig refConfig) {
        this.refConfig.update(refConfig);
        RegistryCenterManage registryManage = SpiClassLoader.getClassLoader(RegistryCenterManage.class).getExtension(registryConfig.getProtocol());
        List<MergeConfig> configs = registryManage.getRegisterServices(registryConfig,refConfig);
        if(CollectionUtil.isEmpty(configs)){
            return;
        }
        serviceChange(registryConfig,configs);

    }

    private Refer<T> getExistingRefer(MergeConfig config, List<Refer<T>> refers) {
        if (refers == null) {
            return null;
        }
        for (Refer<T> r : refers) {
            if (config.equals(r.getConfig())) {
                return r;
            }
        }
        return null;
    }

    private void refreshCluster() {
        List<Refer<T>> refers = new ArrayList<>();
        for (List<Refer<T>> refs : registryRefers.values()) {
            refers.addAll(refs);
        }
        cluster.refresh(refers);
    }

    private void removeRegistry(MergeConfig registryConfig) {
        logger.warn(String.format("Registry %s is remove from %s",registryConfig.identity(),refConfig.identity()));
        registryRefers.remove(registryConfig);
        refreshCluster();

    }

    public void destroy() {
        try {
            RegistryUtil.closeRegistrys(registryConfigs);
            registryRefers.clear();
        } catch (IOException e) {
            logger.error("Exception when destroy cluster: "+refConfig.identity(),e);
        }
        try {
            this.cluster.destroy();
        } catch (Exception e) {
            logger.warn(String.format("Exception when destroy cluster: %s", refConfig.identity()));
        }
    }
}
