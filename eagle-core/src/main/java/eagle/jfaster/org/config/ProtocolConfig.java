package eagle.jfaster.org.config;

import eagle.jfaster.org.config.annotation.ConfigDesc;
import lombok.Getter;
import lombok.Setter;

/**
 * 暴露和订阅协议配置
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class ProtocolConfig extends AbstractConfig {

    // 服务协议
    @Setter
    private String name;

    // 序列化方式
    @Setter
    @Getter
    private String serialization;

    // 协议编码
    @Setter
    @Getter
    private String codec;

    // IO线程池大小
    @Setter
    @Getter
    private Integer selectThreadSize;

    // 最小工作pool线程数
    @Setter
    @Getter
    protected Integer coreWorkerThread;

    // 最大工作pool线程数
    @Setter
    @Getter
    protected Integer maxWorkerThread;

    // 请求响应包的最大长度限制
    @Setter
    @Getter
    protected Integer maxContentLength;

    // server支持的最大连接数
    @Setter
    @Getter
    protected Integer maxServerConnection;

    // 是否延迟init
    @Setter
    @Getter
    protected Boolean lazyInit;

    // 采用哪种cluster 的实现
    @Setter
    @Getter
    protected String cluster;

    // 线程池队列大小
    @Setter
    @Getter
    protected Integer workerQueueSize;

    // proxy type, like jdk or javassist
    @Setter
    @Getter
    protected String proxy;

    // filter, 多个filter用","分割，blank string 表示采用默认的filter配置
    @Setter
    @Getter
    protected String filter;

    // 是否缺省配置
    @Setter
    @Getter
    private Boolean useDefault;

    @Setter
    @Getter
    private String heartbeatFactory;

    @Setter
    @Getter
    private Integer heartbeat;

    @ConfigDesc(excluded = true)
    public String getName() {
        return name;
    }
}
