package eagle.jfaster.org.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 注册中心配置
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class RegistryConfig extends AbstractConfig {

    @Getter
    @Setter
    // 注册配置名称
    private String name;

    @Getter
    @Setter
    // 注册协议
    private String protocol;

    @Getter
    @Setter
    // 注册中心地址，支持多个ip+port，格式：ip1:port1,ip2:port2,ip3，如果没有port，则使用默认的port
    private String address;

    @Getter
    @Setter
    //命名空间
    private String namespace;

    @Getter
    @Setter
    private Integer baseSleepTimeMilliseconds;

    @Getter
    @Setter
    private Integer maxSleepTimeMilliseconds;

    @Getter
    @Setter
    private Integer maxRetries;

    @Getter
    @Setter
    private Integer sessionTimeoutMilliseconds;

    @Getter
    @Setter
    // 注册中心连接超时时间(毫秒)
    private Integer connectionTimeoutMilliseconds;

    @Getter
    @Setter
    // 在该注册中心上服务是否暴露
    private Boolean register;

    @Getter
    @Setter
    // 在该注册中心上服务是否引用
    private Boolean subscribe;
}
