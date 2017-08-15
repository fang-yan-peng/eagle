package eagle.jfaster.org.config;

import eagle.jfaster.org.config.annotation.ConfigDesc;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class MethodConfig extends AbstractConfig {

    @Setter
    // 方法名
    private String name;
    // 超时时间
    @Getter
    @Setter
    private Integer requestTimeout;
    // 失败重试次数（默认为0，不重试）

    @Getter
    @Setter
    private Integer retries;
    // 最大并发调用

    @Getter
    @Setter
    private Integer actives;
    // 参数类型（逗号分隔）

    @Setter
    private String argumentTypes;

    @ConfigDesc(excluded = true)
    public String getName() {
        return name;
    }

    @ConfigDesc(excluded = true)
    public String getArgumentTypes() {
        return argumentTypes;
    }
}
