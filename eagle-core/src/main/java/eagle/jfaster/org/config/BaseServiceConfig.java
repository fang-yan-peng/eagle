package eagle.jfaster.org.config;

import eagle.jfaster.org.config.annotation.ConfigDesc;
import lombok.Setter;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class BaseServiceConfig extends AbstractInterfaceConfig {

    @Setter
    protected String export;

    @ConfigDesc(excluded = true)
    public String getExport() {
        return export;
    }
}
