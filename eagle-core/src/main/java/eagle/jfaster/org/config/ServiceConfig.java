package eagle.jfaster.org.config;

import com.google.common.base.Strings;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Exporter;
import eagle.jfaster.org.rpc.RpcHandler;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.util.CollectionUtil;
import eagle.jfaster.org.util.ConfigUtil;
import eagle.jfaster.org.util.NetUtil;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import static eagle.jfaster.org.constant.EagleConstants.RPC_HANDLER;

/**
 * service 配置和暴露核心逻辑类
 *
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class ServiceConfig<T> extends BaseServiceConfig {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ServiceConfig.class);

    @Setter
    @Getter
    private T ref;

    private Class<T> interfaceClass;

    private List<Exporter<T>> exporters = new CopyOnWriteArrayList<>();

    private List<MergeConfig> registryConfigs;

    @Setter
    @Getter
    private BaseServiceConfig baseService;

    private AtomicBoolean exported = new AtomicBoolean(false);

    public Class<T> getInterface() {
        return interfaceClass;
    }

    public void setInterface(Class<T> interfaceClass) {
        if (interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        this.interfaceClass = interfaceClass;
    }

    public void export() throws Exception {
        if(exported.compareAndSet(false,true)){
            try {
                //检查注册中心
                List<MergeConfig> regConfigs = ConfigUtil.loadRegistryConfigs(getRegistries());
                if( CollectionUtil.isEmpty(regConfigs)){
                    throw new IllegalStateException("Should set registry config for service:" + interfaceClass.getName());
                }
                this.registryConfigs = regConfigs;
                //检查暴露的协议id和端口号
                Set<ProAndPort> proAndPorts = ConfigUtil.parseExport(getExport());
                if(CollectionUtil.isEmpty(proAndPorts)){
                    throw new IllegalStateException("Should set export config for service:" + interfaceClass.getName());
                }
                for(ProAndPort proAndPort : proAndPorts){
                    ProtocolConfig config = null;
                    String protocolId = proAndPort.getProtocolId();
                    int port = proAndPort.getPort();
                    for(ProtocolConfig protocolConfig : protocols){
                        if(protocolId.equals(protocolConfig.getId())){
                            config = protocolConfig;
                            break;
                        }
                    }
                    if(port <=0 || config == null){
                        throw new IllegalStateException(String.format("Port is null or illegal for service:%s" ,interfaceClass.getName()));
                    }
                    doExport(config,port,regConfigs);
                    logger.info(String.format("%s export success,protocol:%s,port:%d",interfaceClass.getName(),config.getName(),port));
                }
            } catch (Exception e) {
                exported.set(false);
                logger.error(String.format("Eport %s error ",interfaceClass.getName()),e);
                throw e;
            }
        }else {
            logger.warn(String.format("%s has explored so ignore it",interfaceClass.getName()));
        }
    }

    private void doExport(ProtocolConfig protocol,int port,List<MergeConfig> regConfigs) throws Exception {
        String protocolName = protocol.getName();
        if(Strings.isNullOrEmpty(protocolName)){
            protocolName = ConfigEnum.protocol.getValue();
        }
        if(Strings.isNullOrEmpty(host) && baseService != null){
            host = baseService.getHost();
        }
        if(NetUtil.isInvalidLocalHost(host)){
            host = ConfigUtil.getLocalHostAddress(regConfigs);
        }
        MergeConfig serviceConfig = new MergeConfig();
        serviceConfig.setHost(host);
        serviceConfig.setPort(port);
        serviceConfig.setInterfaceName(interfaceClass.getName());
        serviceConfig.setProtocol(protocolName);
        serviceConfig.setVersion(Strings.isNullOrEmpty(version)? ConfigEnum.version.getValue() : version);
        serviceConfig.addExt(ConfigEnum.refreshTimestamp.getName(),String.valueOf(System.currentTimeMillis()));
        ConfigUtil.collectConfigParams(serviceConfig,protocol,baseService,this);
        RpcHandler rpcHandler = SpiClassLoader.getClassLoader(RpcHandler.class).getExtension(RPC_HANDLER);
        exporters.add(rpcHandler.export(interfaceClass,ref,serviceConfig,regConfigs));

    }

    public void unExport(){
        if(exported.compareAndSet(true,false)){
            try {
                RpcHandler rpcHandler = SpiClassLoader.getClassLoader(RpcHandler.class).getExtension(RPC_HANDLER);
                rpcHandler.unexport(exporters,registryConfigs);
            } catch (Exception e) {
                logger.warn(String.format("%s unExport error",interfaceClass.getName()),e);
            }
        }
    }
}
