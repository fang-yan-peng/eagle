package eagle.jfaster.org.cluster.ha;

import eagle.jfaster.org.cluster.HaStrategy;
import eagle.jfaster.org.config.common.MergeConfig;

/**
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
public abstract class AbstractHaStrategy<T> implements HaStrategy<T> {

    protected MergeConfig config;

    @Override
    public void setConfig(MergeConfig config) {
        this.config = config;
    }
}

