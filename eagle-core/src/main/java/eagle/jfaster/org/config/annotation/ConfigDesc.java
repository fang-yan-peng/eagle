package eagle.jfaster.org.config.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigDesc {

    String key() default "";

    boolean excluded() default false;

    boolean required() default false;
}
