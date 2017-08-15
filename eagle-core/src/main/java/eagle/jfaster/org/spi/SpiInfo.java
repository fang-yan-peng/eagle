package eagle.jfaster.org.spi;

import java.lang.annotation.*;

/**
 * spi实现类标识
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpiInfo {

    String name() default "";

    String dependency() default "";

    Class<?> dependencyType() default Spi.class;
}
