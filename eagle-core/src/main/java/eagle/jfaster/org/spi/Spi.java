package eagle.jfaster.org.spi;

import java.lang.annotation.*;

/**
 * spi接口标识
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Spi {
    Scope scope() default Scope.SINGLETON;
}
