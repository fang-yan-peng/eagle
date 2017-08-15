package eagle.jfaster.org.registry.factory;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.registry.RegistryCenter;
import eagle.jfaster.org.registry.ServiceChangeListener;
import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
@Spi(scope = Scope.SINGLETON)
public interface RegistryCenterManage {

    RegistryCenter getRegistry(MergeConfig regConfig);

    void registerService(MergeConfig regConfig,MergeConfig serviceConfig);

    void registerRef(MergeConfig regConfig,MergeConfig refConfig);

    void addServiceListener(MergeConfig regConfig,MergeConfig refConfig,ServiceChangeListener listener);

    void addRefListener(MergeConfig regConfig,MergeConfig refConfig,ServiceChangeListener listener);

    List<MergeConfig> getRegisterServices(MergeConfig regConfig,MergeConfig refConfig);

    List<MergeConfig> getSubscribeServices(MergeConfig regConfig,MergeConfig refConfig);

}
