package eagle.jfaster.org.config;

import static eagle.jfaster.org.constant.EagleConstants.*;
import lombok.Getter;

/**
 * 默认配置
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */

public enum ConfigEnum {

    version("version", DEFAULT_VERSION),
    requestTimeout("requestTimeout", 200),
    connectTimeout("connectTimeout", 30000l),
    coreWorkerThread("coreWorkerThread", 20),
    maxWorkerThread("maxWorkerThread", 200),
    maxInvokeError("maxInvokeError", 10),
    minClientConnection("minClientConnection", 2),
    maxClientConnection("maxClientConnection", 10),
    maxContentLength("maxContentLength", 10 * 1024 * 1024),
    maxLifetime("maxLifetime", MAX_LIFETIME),
    maxServerConnection("maxServerConnection", 100000),
    idleTime("idleTime", 15*60*1000l),
    heartbeat("heartbeat", 15*60*1000l),
    compress("compress", false),
    minCompressSize("minCompressSize", 1000), // 进行gz压缩的最小数据大小。超过此阈值才进行gz压缩
    callback("callback", ""),
    lazyInit("lazyInit", false),
    protect("protect", false),

    /************************** SPI start ******************************/

    serialization("serialization", "kryo"),
    codec("codec", "eagle"),
    heartbeatFactory("heartbeatFactory", "eagle"),

    /************************** SPI end ******************************/

    group("group", "default_rpc"),
    statsLog("statsLog", ""),
    useNative("useNative",true),

    // 0为不做并发限制
    actives("actives", 0),
    activesWait("actactiveWait", 3000l),

    refreshTimestamp("refreshTimestamp", 0L),

    // 格式为protocol:port
    export("export", ""),

    check("check", false),
    address("address", ""),
    namespace("namespace", "eagle"),
    baseSleepTimeMilliseconds("baseSleepTimeMilliseconds", 1000),
    maxSleepTimeMilliseconds("maxSleepTimeMilliseconds", 3000),
    maxRetries("maxRetries", 3),
    sessionTimeoutMilliseconds("sessionTimeoutMilliseconds", 0),
    connectionTimeoutMilliseconds("connectionTimeoutMilliseconds", 0),
    digest("digest", ""),

    cluster("cluster", CLUSTER_DEFAULT),
    loadbalance("loadbalance", "activeWeight"),
    haStrategy("haStrategy", "failover"),
    protocol("protocol", PROTOCOL_DEFAULT),
    useDefault("useDefault", false),

    host("host", ""),
    port("port", 0),
    workerQueueSize("workerQueueSize", 10),
    selectThreadSize("selectThread",Runtime.getRuntime().availableProcessors()*2),
    filter("filter", ""),



    application("application", APPLICATION_DEFAULT),
    module("module", MODULE_DEFAULT),

    retries("retries", 0),
    async("async", false),
    callbackThread("callbackThread", DEFAULT_WORKER_THREAD),
    callbackQueueSize("callbackQueueSize", DEFAULT_QUEUE_SIZE),
    callbackWaitTime("callbackWaitTime", 4000),

    register("register", true),
    subscribe("subscribe", true),
    throwException("throwException", "true"),

    weight("weight", 100),
    disable("disable", false);


    private ConfigEnum(String name , String value){
        this.name = name;
        this.value = value;
    }

    private ConfigEnum(String name , int intValue){
        this.name = name;
        this.intValue = intValue;
    }

    private ConfigEnum(String name , long longValue){
        this.name = name;
        this.longValue = longValue;
    }

    private ConfigEnum(String name , boolean boolValue){
        this.name = name;
        this.booleanValue = boolValue;
    }

    @Getter
    private String name;

    @Getter
    private String value;

    @Getter
    private int intValue;

    @Getter
    private long longValue;

    @Getter
    private boolean booleanValue;

}
