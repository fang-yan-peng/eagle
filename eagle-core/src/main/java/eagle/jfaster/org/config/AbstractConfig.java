package eagle.jfaster.org.config;

import eagle.jfaster.org.config.annotation.ConfigDesc;
import lombok.Setter;

/**
 * Created by fangyanpeng1 on 2017/8/7.
 */
public abstract class AbstractConfig{

    @Setter
    private String id;

    @ConfigDesc(excluded = true)
    public String getId() {
        return id;
    }
}
