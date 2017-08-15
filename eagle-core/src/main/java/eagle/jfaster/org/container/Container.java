package eagle.jfaster.org.container;

import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

/**
 * Created by fangyanpeng1 on 2017/8/14.
 */
@Spi(scope = Scope.SINGLETON)
public interface Container {
    /**
     * start.
     */
    void start();

    /**
     * stop.
     */
    void stop();
}
