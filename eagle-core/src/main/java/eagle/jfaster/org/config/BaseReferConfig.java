package eagle.jfaster.org.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng1 on 2017/8/8.
 */
public class BaseReferConfig extends AbstractInterfaceConfig{

    // 请求超时
    @Setter
    @Getter
    protected Integer requestTimeout;

    // 连接超时
    @Setter
    @Getter
    protected Long connectTimeout;

    // client最小连接数
    @Setter
    @Getter
    protected Integer minClientConnection;

    // client最大连接数
    @Setter
    @Getter
    protected Integer maxClientConnection;

    @Setter
    @Getter
    protected Long idleTime;

    @Setter
    @Getter
    protected Long maxLifetime;

    @Setter
    @Getter
    protected Integer maxInvokeError;

    @Getter
    @Setter
    // 是否开启gzip压缩
    protected Boolean compress;

    @Getter
    @Setter
    // 进行gzip压缩的最小阈值，且大于此值时才进行gzip压缩。单位Byte
    protected Integer minCompressSize;

    // loadbalance 方式
    @Setter
    @Getter
    protected String loadbalance;

    // 高可用策略
    @Setter
    @Getter
    protected String haStrategy;


}
