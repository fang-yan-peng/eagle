package eagle.jfaster.org.registry;

import eagle.jfaster.org.config.common.MergeConfig;
import java.util.List;

public interface ServiceChangeListener {

    void serviceChange(MergeConfig registryConfig, List<MergeConfig> serviceConfig);
    void refChange(MergeConfig registryConfig, MergeConfig refConfig);

}
