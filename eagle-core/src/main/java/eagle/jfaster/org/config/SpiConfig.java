package eagle.jfaster.org.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng1 on 2017/8/13.
 */
public class SpiConfig<T> extends AbstractConfig {

    private Class<T> interfaceClass;

    @Setter
    @Getter
    private Class<T> spiClass;

    public Class<T> getInterface() {
        return interfaceClass;
    }

    public void setInterface(Class<T> interfaceClass) {
        if (interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        this.interfaceClass = interfaceClass;
    }
}
