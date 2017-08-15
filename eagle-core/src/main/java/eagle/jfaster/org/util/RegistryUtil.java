package eagle.jfaster.org.util;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.registry.factory.RegistryCenterManage;
import eagle.jfaster.org.spi.SpiClassLoader;

import java.io.IOException;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/6.
 */
public class RegistryUtil {

    public static void closeRegistrys(List<MergeConfig> registryConfigs) throws IOException {
        if(CollectionUtil.isEmpty(registryConfigs)){
            return;
        }
        RegistryCenterManage registryManage;
        for(MergeConfig regConfig : registryConfigs){
            registryManage = SpiClassLoader.getClassLoader(RegistryCenterManage.class).getExtension(regConfig.getProtocol());
            registryManage.getRegistry(regConfig).close();

        }
    }
}
