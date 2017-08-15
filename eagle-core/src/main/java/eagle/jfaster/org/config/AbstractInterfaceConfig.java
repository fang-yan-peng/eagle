package eagle.jfaster.org.config;

import eagle.jfaster.org.config.annotation.ConfigDesc;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ref和service公共配置
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class AbstractInterfaceConfig extends AbstractConfig {

    @Getter
    @Setter
    protected List<ProtocolConfig> protocols;

    @Getter
    @Setter
    // 注册中心的配置列表
    protected List<RegistryConfig> registries;

    @Getter
    @Setter
    // 应用名称
    protected String application;

    @Getter
    @Setter
    // 模块名称
    protected String module;

    @Getter
    @Setter
    // 分组
    protected String group;

    @Setter
    // 服务版本
    protected String version;

    @Getter
    @Setter
    // 代理类型
    protected String proxy;

    @Getter
    @Setter
    // 过滤器
    protected String filter;

    @Getter
    @Setter
    // 最大并发调用
    protected Integer actives;

    @Getter
    @Setter
    // 并发等待时间
    protected Long activesWait;

    @Getter
    @Setter
    // 是否异步
    protected Boolean async;

    @Getter
    @Setter
    // 服务接口的失败mock实现类名
    protected String mock;

    @Getter
    @Setter
    // 是否注册
    protected Boolean register;

    @Getter
    @Setter
    // 是否注册
    protected Boolean subscribe;

    @Getter
    @Setter
    // 是否记录访问日志，true记录，false不记录
    protected String accessLog;

    @Getter
    @Setter
    // 是否进行check，如果为true，则在监测失败后抛异常
    protected String check;

    @Getter
    @Setter
    // 重试次数
    protected Integer retries;

    @Setter
    protected String host;

    @ConfigDesc(excluded = true)
    public String getVersion() {
        return version;
    }

    @ConfigDesc(excluded = true)
    public String getHost() {
        return host;
    }
}
