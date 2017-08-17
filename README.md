# Eagle

# 概要
Eagle是一个分布式的RPC框架，支持灵活的配置，支持kryo、hessian等序列化协议，默认序列化使用kryo。

# 特点
- 需要很少的操作就可以实现自己的分布式rpc调用。
- 借助zookeeper实现服务注册和发现。
- 基于AQS实现高性能连接池。
- 提供failover和failfast两种高可用策略。
- 支持同步和异步回调两种机制。

# 例子

> 运行要求:
>  * JDK 1.7 or above
>  * 编译工具 [Maven][maven] or [Gradle][gradle]

## 同步调用

1. 添加依赖.

   ```xml
    <dependency>
        <groupId>eagle.jfaster.org</groupId>
        <artifactId>eagle-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>eagle.jfaster.org</groupId>
        <artifactId>eagle-registry-zookeeper</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>eagle.jfaster.org</groupId>
        <artifactId>eagle-transport-netty</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>eagle.jfaster.org</groupId>
        <artifactId>eagle-spring-support</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
   ```

2. 创建一个接口类。

    `src/main/java/eagle/jfaster/org/service/Calculate.java`

    ```java
    package eagle.jfaster.org.service;

    public interface Calculate {

        int add(int a,int b);

        int sub(int a,int b);
    }

    ```

3. 实现接口，并暴露服务。
    
    `src/main/java/eagle/jfaster/org/service/impl/CalculateImpl.java`
    
    ```java
    package eagle.jfaster.org.service.impl;

    import eagle.jfaster.org.service.Calculate;
    import org.springframework.stereotype.Service;

    @Service("calculate")
    public class CalculateImpl implements Calculate {

        public int add(int a, int b) {
            return a+b;
        }

        public int sub(int a, int b) {
            return a-b;
        }
    }

    ```

    `src/main/resources/server.xml`
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:context="http://www.springframework.org/schema/context"
           xmlns:eagle="http://eagle.jfaster.org/schema/eagle"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
                            http://www.springframework.org/schema/beans/spring-beans.xsd
                            http://www.springframework.org/schema/context
                            http://www.springframework.org/schema/context/spring-context.xsd
                            http://eagle.jfaster.org/schema/eagle
                            http://eagle.jfaster.org/schema/eagle/eagle.xsd
                            ">

        <context:component-scan base-package="eagle.jfaster.org" />
        <context:annotation-config/>

        <!--注册中心配置可以多个-->
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
        <!--协议配置-->
        <eagle:protocol id="proto" name="eagle" serialization="kryo" use-default="true" max-content-length="16777216" max-server-connection="20000" core-worker-thread="20" max-worker-thread="400" worker-queue-size="10"/>
        <eagle:base-service id="baseService" group="eagleGroup" export="proto:9200" registry="regCenter"/>
        <eagle:service interface="eagle.jfaster.org.service.Calculate" ref="calculate" base-service="baseService" export="proto:9300,proto:9400"/>
    ```
    
    `src/main/java/eagle/jfaster/org/Server.java`
    
    ```java

    package eagle.jfaster.org.server;

    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;

    import java.util.concurrent.CountDownLatch;

    public class Server {
        public static void main(String[] args) throws InterruptedException {
            ApplicationContext appCtx = new ClassPathXmlApplicationContext("server.xml");
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
        }
    }

    ```
    执行main方法，就会在9300和9400端口发布服务。同时eagle还提供了eagle.jfaster.org.container.Main类，会跟据环境变量eagle.container的设置启动不同的容器。
    如果没有配置会默认启动SpringContainer，会加载classpath*:META-INF/spring/*.xml的所有spring配置文件。

4. 创建和启动客户端

    `src/main/resources/client_sync.xml`

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:eagle="http://eagle.jfaster.org/schema/eagle"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
                            http://www.springframework.org/schema/beans/spring-beans.xsd
                            http://www.springframework.org/schema/context
                            http://www.springframework.org/schema/context/spring-context.xsd
                            http://eagle.jfaster.org/schema/eagle
                            http://eagle.jfaster.org/schema/eagle/eagle.xsd
                            ">

        <context:component-scan base-package="eagle.jfaster.org" />
        <context:annotation-config/>
        <!--注册中心配置可以多个-->
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
        <!--协议配置-->
        <eagle:protocol name="eagle" serialization="kryo" use-default="true" cluster="eagle" max-content-length="16777216"/>
        <eagle:base-refer id="baseRefer" request-timeout="300" actives="20000" actives-wait="300" loadbalance="roundrobin" ha-strategy="failfast" protocol="eagle" registry="regCenter" compress="false" group="eagleGroup" connect-timeout="10000"/>
        <eagle:refer id="cal" interface="eagle.jfaster.org.service.Calculate" base-refer="baseRefer" max-invoke-error="10" max-client-connection="200" />

    ```

    `src/main/java/eagle/jfaster/org/client/SyncClient.java`

    ```java
    package eagle.jfaster.org.client;

    import eagle.jfaster.org.service.Calculate;
    import eagle.jfaster.org.service.HelloWorld;
    import eagle.jfaster.org.service.Notify;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;

    public class SyncClient {
        public static void main(String[] args) {
            ApplicationContext appCtx = new ClassPathXmlApplicationContext("client_sync.xml");
            Calculate calculate = appCtx.getBean("cal",Calculate.class);
            System.out.println(calculate.add(1,3));
            System.out.println(calculate.sub(8,3));
        }
    }

    ```
    执行main方法，就会在控制台打印出信息。

## 异步调用

1. 异步调用只需在客户端注册一个MethodInvokeCallBack即可，服务端不用改动，在回调实例中可以引用任意spring容器中的实例。


    `src/main/java/eagle/jfaster/org/callback/CalculateCallBack.java`

    ```java
    package eagle.jfaster.org.callback;

    import eagle.jfaster.org.rpc.MethodInvokeCallBack;
    import javax.annotation.Resource;
    public class CalculateCallBack implements MethodInvokeCallBack<Integer> {

        @Resource
        CalculateDao calculateDao;

        public void onSuccess(Integer response) {
            calculateDao.insert(response);
            System.out.println("calculate res:"+response);
        }

        public void onFail(Exception e) {
            e.printStackTrace();
        }
    }

    ```

    `src/main/java/eagle/jfaster/org/callback/CalculateDao.java`

    ```java
    package eagle.jfaster.org.callback;

    import org.springframework.stereotype.Service;

    @Service("calculateDao")
    public class CalculateDao {
        void insert(Integer i){
            System.out.println("-----------insert--------"+i);
        }
    }

    ```

2. 创建和启动客户端。

    `src/main/resources/client_async.xml`

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:context="http://www.springframework.org/schema/context"
           xmlns:eagle="http://eagle.jfaster.org/schema/eagle"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
                            http://www.springframework.org/schema/beans/spring-beans.xsd
                            http://www.springframework.org/schema/context
                            http://www.springframework.org/schema/context/spring-context.xsd
                            http://eagle.jfaster.org/schema/eagle
                            http://eagle.jfaster.org/schema/eagle/eagle.xsd
                            ">

        <context:component-scan base-package="eagle.jfaster.org" />
        <context:annotation-config/>
        <!--注册中心配置可以多个-->
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
        <!--协议配置-->
        <eagle:protocol name="eagle" serialization="kryo" use-default="true" cluster="eagle" max-content-length="80000000" />
        <eagle:base-refer id="baseReferAsync" request-timeout="300" actives="20000" actives-wait="3000" loadbalance="roundrobin" ha-strategy="failfast" protocol="eagle" registry="regCenter" compress="false" group="eagleGroup" connect-timeout="10000"/>
        <eagle:refer id="calAsync" callback="eagle.jfaster.org.callback.CalculateCallBack" interface="eagle.jfaster.org.service.Calculate" base-refer="baseReferAsync" max-invoke-error="10" max-client-connection="200" />

    </beans>
    ```

   `src/main/java/eagle/jfaster/org/client/AsyncClient.java`

   ```java

   package eagle.jfaster.org.client;

   import eagle.jfaster.org.service.Calculate;
   import org.springframework.context.ApplicationContext;
   import org.springframework.context.support.ClassPathXmlApplicationContext;

   import java.util.concurrent.TimeUnit;
   public class AsyncClient {
       public static void main(String[] args) throws InterruptedException {
           ApplicationContext appCtx = new ClassPathXmlApplicationContext("client_async.xml");
           Calculate calculate = appCtx.getBean("calAsync",Calculate.class);
           calculate.add(1,3);
           calculate.sub(34,9);
           //等待异步结果
           while (true) {
               TimeUnit.SECONDS.sleep(5);
           }
       }
   }
   ```
   运行结果如果成功会调用MethodInvokeCallBack的onSuccess方法，否则会调用onFail方法。不要使用异步客户端返回的值，那是不正确的，正确的值通过回调的onSuccess方法获取。

# eagle常用配置

## 注册中心的配置（eagle:registry）
    1、name: 注册中心的名称，如果没有配置id，会用name当id。
    2、protocol: 注册中心协议，目前只支持zookeeper。
    3、address: 注册中心地址，如果是多个地址以逗号分隔，如果是多组用|或;分隔。
    4、namespace: zk上的命名空间，所有的信息都在改命名空间下。
    5、max-retries: 连接注册中心的重试次数。
    6、base-sleep-time-milliseconds: 重试时间间隔
    7、max-sleep-time-milliseconds: 最大重试时间
    8、session-timeout-milliseconds: 与注册中心的会话超时时间
    9、digest: 连接注册中心的密码

## 协议配置(eagle:protocol）
    1、name: 协议名称,目前只支持eagle，后续加入thrift，如果没有配置id，name会充当id。
    2、serialization: 序列化，支持hessian、kryo、protobuf序列化协议。
    3、heartbeat-factory: 心跳工厂,默认值是eagle,通过spi方式可以自定义心跳工厂。
    4、select-thread-size: netty处理io的线程数，尽量不要阻塞netty的io线程。
    5、core-worker-thread: 处理业务的核心线程数。
    6、max-worker-thread:  处理业务的最大线程数。
    7、max-content-length: rpc调用最大传输的字节数。
    8、max-server-connection: 一个端口支持的最大连接数。
    9、codec: 用于编码和解码的工具类，默认调用EagleRpcCodec，可以通过spi的方式自定义codec。
    10、use-native: 在linux环境下，是否开启epoll。默认是true。

## 客户端配置（eagle:refer）
    1、group: 调用组，客户端和服务端配置要一致。
    2、version: 版本号，区分相同服务的不同版本，客户端与服务端的版本号一致才能调用成功。
    3、retries: 调用失败重试次数
    4、actives: 支持的最大并发数。
    5、actives-wait: 并发达到最大后等待多长时间。
    6、check: 启动时是否检测有服务，默认false。
    7、registry: 注册中心，多个注册中心以逗号分隔。
    8、host: ip地址，一般不需要指定，系统会自动获取，如果特殊需求可自己设定。
    9、request-timeout: 请求超时时间
    10、min-client-connection: 最小连接数
    11、max-client-connection: 最大连接数
    12、idle-time: 连接空闲多长时间会被回收
    13、connect-timeout: 获取连接的超时时间
    14、max-invoke-error: 连续调用失败的的次数，超过这个次数，这个服务设置为不可用。
    15、compress: 是否开启gzip压缩
    16、loadbalance: 负载均衡策略，目前支持random、roundrobin、activeWeigth
    17、ha-strategy: ha策略，目前支持failover、failfast。
    18、interface: 服务的接口
    19、callback: 回调，如果设置了回调，该服务就会变成异步。
    20、base-refer: 公共的refer配置。

## 服务端配置（eagle:service）
    1、group: 调用组，客户端和服务端配置要一致。
    2、version: 版本号，区分相同服务的不同版本，客户端与服务端的版本号一致才能调用成功。
    3、registry: 注册中心，多个注册中心以逗号分隔。
    4、host: ip地址，一般不需要指定，系统会自动获取，如果特殊需求可自己设定。
    5、interface: 服务的接口
    6、base-service: 公共的service配置。
    7、ref: 接口的实现类引用
    8、class: 如果没有配置ref，会根据class加载接口实现类。
    9、export: 服务暴露的协议和端口号，多个用逗号分割，如proto:7000,proto:8000，proto是协议的id。

# 日志配置
    尽量制定框架日志，如果不指定框架内部会自动获取日志实现。
    以logback为例:

    ```xml
    <appender name="logFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <FileNamePattern>/letv/logs/lepay_boss_%d{yyyyMMdd}.log</FileNamePattern>
            <MaxHistory>90</MaxHistory>
        </rollingPolicy>
        <encoder>
            <Pattern>%d{HH:mm:ss} %level [%thread] [%logger{5}] - %msg%n</Pattern>
            <charset>utf-8</charset>
        </encoder>
    </appender>

    <!-- 异步输出 -->
    <appender name="rollingLogFile" class="ch.qos.logback.classic.AsyncAppender">
        <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        <queueSize>2048</queueSize>
        <!-- 添加附加的appender,最多只能添加一个 -->
        <appender-ref ref="logFile"/>
    </appender>

    <logger name="eagle.jfaster.org"   level="debug"  additivity="false">
         <appender-ref ref="rollingLogFile" />
    </logger>
   ```
# 贡献者

* fangyanpeng([@fangyanpeng](https://github.com/fang-yan-peng))

[maven]:https://maven.apache.org
[gradle]:http://gradle.org
[zookeeper]:http://zookeeper.apache.org


