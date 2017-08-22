package eagle.jfaster.org.config.annotation;

import java.lang.annotation.*;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    String id() default "";

    String protocol() default "";

    // 注册中心的配置列表
    String registry() default "";

    // 应用名称
    String application() default "";

    // 模块名称
    String module() default "";

    // 分组
    String group() default "";

    // 服务版本
    String version() default "";

    // 过滤器
    String filter() default "";

    // 最大并发调用
    String actives()default "";

    // 并发等待时间
    String activesWait()default "";

    // 服务接口的失败mock实现类名
    String mock()default "";

    // 是否注册
    String register()default "";

    // 是否记录访问日志，true记录，false不记录
    String statsLog()default "";

    // 重试次数
    String retries()default "";

    String host()default "";

    String export()default "";

    String baseService()default "";

    String weight() default "";

}
