# Eagle

# 概要
Eagle是一个分布式的RPC框架，支持灵活的配置，支持分布式追踪，支持[Kryo][kryo]、[Hessian][hessian]等序列化协议，默认序列化使用kryo。提供Spring和SpringBoot插件，方便与Spring和SpringBoot集成。

# 特点
- 借助[Zookeeper][zookeeper]实现服务注册和发现。
- 分布式ID追踪，方便排查问题。
- 基于AQS实现高性能连接池。
- 提供failover和failfast两种高可用策略。
- 支持同步和异步回调两种机制。
- 提供接口方法请求时间、tps等监控信息。
- 提供和自定义服务端过载保护策略。

# jmh基准测试结果
> 运行基准测试步骤：
>  * cd eagle-benchmark
>  * mvn clean install
>  * cd eagle-benchmark-server/target
>  * tar -zxvf eagle-benchmark-server-1.4-assembly.tar.gz
>  * cd eagle-benchmark-server-1.4
>  * bin/start.sh
>  * cd eagle-benchmark/eagle-benchmark-client
>  * 在linux上运行 sh benchmark.sh，在window上运行 benchmark.cmd
>  * 注（修改benchmark.sh 中的 -t 参数修改线程数）
   
> macOs上的结果（16g内存，2.5 GHz Intel Core i7），启动16个线程。
   
   ![Image text](https://raw.githubusercontent.com/fang-yan-peng/eagle/master/benchmark_mac.jpeg)

> linux上的结果（64g内存，24核），启动80个线程。
   
   ![Image text](https://raw.githubusercontent.com/fang-yan-peng/eagle/master/benchmark_linux.jpeg)


# 例子
> 内置zookeeper，仅测试使用。生产环境请更换真实zookeeper地址，使用内置zookeeper有可能会报连接警告或者异常，更换真实zk地址即可。运行要求:
>  * JDK 1.7 or above
>  * 编译工具 [Maven][maven] or [Gradle][gradle]

## 添加依赖
   如果是spring,添加如下:
   ```xml
    <dependency>
        <groupId>org.jfaster.eagle</groupId>
        <artifactId>eagle-core</artifactId>
        <version>1.4</version>
    </dependency>
    <dependency>
        <groupId>org.jfaster.eagle</groupId>
        <artifactId>eagle-registry-zookeeper</artifactId>
        <version>1.4</version>
    </dependency>
    <dependency>
        <groupId>org.jfaster.eagle</groupId>
        <artifactId>eagle-transport-netty</artifactId>
        <version>1.4</version>
    </dependency>
    <dependency>
        <groupId>org.jfaster.eagle</groupId>
        <artifactId>eagle-spring-support</artifactId>
        <version>1.4</version>
    </dependency>
   ```
   如果是springBoot,添加如下:
  ```xml
   <dependency>
       <groupId>org.jfaster.eagle</groupId>
       <artifactId>eagle-core</artifactId>
       <version>1.4</version>
   </dependency>
   <dependency>
       <groupId>org.jfaster.eagle</groupId>
       <artifactId>eagle-registry-zookeeper</artifactId>
       <version>1.4</version>
   </dependency>
   <dependency>
       <groupId>org.jfaster.eagle</groupId>
       <artifactId>eagle-transport-netty</artifactId>
       <version>1.4</version>
   </dependency>
   <dependency>
     <groupId>org.jfaster.eagle</groupId>
     <artifactId>spring-boot-starter-eagle</artifactId>
     <version>1.4</version>
   </dependency>
  ```
## 分布式调用追踪
```xml
在应用中一个接口通常会涉及到一系列服务的rpc调用，由于服务分布式部署导致出现问题排查相对困难。eagle框架在一次调用中生成的traceId是相同的，只要通过ELK等日志搜集系统把日志集中处理，那么输入traceId就可以获取整个链路的调用过程。
          C
         /    
例如 A - B   A调用B，B又调用C和D，那么在一次调用中，可以通过traceId，把整个调用串联起来。traceId是根据ip+进程号+时间戳+计数，生成的全局唯一id，如果框架内部获取的ip不准确可以通过，如-Dhost=10.110.69.17指定。
         \
          D
在业务代码中，当打印日志的时候，可以通过TraceContex.getOpaque()方法获取当前调用链中的traceId。打印出traceId，logger.info(TraceContex.getOpaque() + "xxxxxx")。这样就可以根据日志追踪整个调用过程。
更简单的方式是使用eagle框架提供的日志组件，配置如下：
```
### logback的配置
- 如果当前上下文中存在traceId，logback将在输出traceId，如果不存在traceId则输出N/A。用%traceId来展示traceId。
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
        <layout class="eagle.jfaster.org.trace.logback.TraceIdPatternLogbackLayout">
            <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%traceId] [%thread] %-5level %logger{36} -%msg%n</Pattern>
        </layout>
    </encoder>
</appender>
```
### log4j的配置
- 配置layout
```xml
log4j.appender.CONSOLE.layout=eagle.jfaster.org.trace.log4j.TraceIdPatternLayout
```
- 在layout.ConversionPattern中设置 %T来展示traceId
```xml
log4j.appender.CONSOLE.layout.ConversionPattern=%d [%T] %-5p %c{1}:%L - %m%n
```

### 分布式追踪在程序中的应用

- 在spring配置文件中，添加`<eagle:trace/>` 配置，springboot不用添加额外配置。然后在需要追踪的类或者方法上打上@Trace注解。

`src/main/java/eagle/jfaster/org/controller/TraceController.java`

```java
    package eagle.jfaster.org.controller;
    
    import eagle.jfaster.org.config.annotation.Refer;
    import eagle.jfaster.org.service.Calculate;
    import eagle.jfaster.org.service.Hello;
    import eagle.jfaster.org.trace.annotation.Trace;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    
    /**
     * Created by fangyanpeng on 2017/12/16.
     */
    @RestController
    @RequestMapping("/")
    public class TraceController {
    
        private static final Logger logger = LoggerFactory.getLogger(TraceController.class);
    
        @Refer(baseRefer = "baseRefer")
        private Calculate calculate;
    
        @Refer(baseRefer = "baseRefer")
        private Hello hello;
    
        @Trace
        @RequestMapping("/cal")
        public String cal(@RequestParam int a, @RequestParam int b,@RequestParam int code){
            //log会打印出TraceId
            logger.info(hello.hello(code));
            int res = calculate.add(a, b);
            logger.info("calculate {}",res);
            return String.valueOf(res);
        }
    }

```


## 同步调用

1. 创建一个接口类。

    `src/main/java/eagle/jfaster/org/service/Calculate.java`

    ```java
    package eagle.jfaster.org.service;

    public interface Calculate {

        int add(int a,int b);

        int sub(int a,int b);
    }

    ```

2. 实现接口，并暴露服务。
    
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
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:4181" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
        <!--协议配置-->
        <eagle:protocol id="proto" name="eagle" serialization="kryo" use-default="true" max-content-length="16777216" max-server-connection="20000" core-worker-thread="20" max-worker-thread="200" worker-queue-size="10"/>
        <eagle:base-service id="baseService" group="eagleGroup" export="proto:9200" registry="regCenter"/>
        <eagle:service interface="eagle.jfaster.org.service.Calculate" ref="calculate" base-service="baseService" export="proto:9300,proto:9400" service-type="cglib"/>
    ```
    
    `src/main/java/eagle/jfaster/org/Server.java`
    
    ```java

    package eagle.jfaster.org.server;

    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;

    import java.util.concurrent.CountDownLatch;

    public class Server {
        public static void main(String[] args) throws InterruptedException {
            //启动Curator框架提供的内置zookeeper 仅供测试使用，生产环境请使用真实zookeeper地址
            EmbedZookeeperServer.start(4181);
            ApplicationContext appCtx = new ClassPathXmlApplicationContext("server.xml");
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
        }
    }

    ```
    执行main方法，就会在9300和9400端口发布服务。同时eagle还提供了eagle.jfaster.org.container.Main类，会跟据环境变量eagle.container的设置启动不同的容器。
    如果没有配置会默认启动SpringContainer，会加载classpath*:META-INF/spring/*.xml的所有spring配置文件。

3. 创建和启动客户端

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
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:4181" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
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
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:4181" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
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

## 注解的方式

1. 创建接口

    `src/main/java/eagle/jfaster/org/service/Hello.java`

    ```java
    
    package eagle.jfaster.org.service;
        
    public interface Hello {
        String hello();
    }
    
    ```
2. 实现接口，并打上Service注解。
    
    `src/main/java/eagle/jfaster/org/anno/HelloImpl.java`
    
    ```java
    package eagle.jfaster.org.anno;
    
    import eagle.jfaster.org.config.annotation.Service;
    import eagle.jfaster.org.service.Hello;
    
    @Service(baseService = "baseService",export = "proto:28000", serviceType = "cglib")
    public class HelloImpl implements Hello {
    
        public String hello() {
            return "hello eagle";
        }
    }

    ```
3. 创建和启动服务端。

    `src/main/resources/server_annotation.xml`
            
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
    
        <context:component-scan base-package="eagle.jfaster.org.anno" />
    
        <context:annotation-config/>
    
        <!--注册中心配置可以多个-->
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:4181" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
    
        <!--协议配置-->
        <eagle:protocol id="proto" name="eagle" serialization="kryo" use-default="true" max-content-length="16777216" max-server-connection="20000" core-worker-thread="20" max-worker-thread="200" worker-queue-size="10"/>
    
        <eagle:base-service id="baseService" group="eagleGroup" export="proto1:9200" registry="regCenter"/>
    
        <eagle:component-scan base-package="eagle.jfaster.org.anno"/>

    ```
    
    `src/main/java/eagle/jfaster/org/server/ServerAnnotation.java`

    ```java
    package eagle.jfaster.org.server;
    
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    import java.util.concurrent.CountDownLatch;
    public class ServerAnnotation {
     
     
        public static void main(String[] args) throws InterruptedException {
            //启动Curator框架提供的内置zookeeper 仅供测试使用，生产环境请使用真实zookeeper地址         
            EmbedZookeeperServer.start(4181);
            ApplicationContext appCtx = new ClassPathXmlApplicationContext("server_annotation.xml");
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
        }
    }

    ```

4. 创建和启动客户端。

    `src/main/resources/client_annotation.xml`

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
    
        <context:component-scan base-package="eagle.jfaster.org.client" />
    
        <context:annotation-config/>
    
        <!--注册中心配置可以多个-->
        <eagle:registry name="regCenter" protocol="zookeeper"  address="127.0.0.1:4181" namespace="eagle" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3"/>
    
        <!--协议配置-->
        <eagle:protocol name="eagle" serialization="kryo" use-default="true" cluster="eagle" max-content-length="16777216"/>
    
        <eagle:base-refer id="baseRefer" request-timeout="300" actives="20000" actives-wait="300" loadbalance="roundrobin" ha-strategy="failfast" protocol="eagle" registry="regCenter" compress="false" group="eagleGroup" connect-timeout="10000"/>
    
        <eagle:component-scan base-package="eagle.jfaster.org.client"/>
    
    </beans>
    ```

   `src/main/java/eagle/jfaster/org/client/AnnotationClient.java`

   ```java
   package eagle.jfaster.org.client;
   
   import eagle.jfaster.org.config.annotation.Refer;
   import eagle.jfaster.org.service.Hello;
   import org.springframework.context.support.ClassPathXmlApplicationContext;
   import org.springframework.stereotype.Service;
   
   /**
    * Created by fangyanpeng on 2017/8/18.
    */
   @Service
   public class AnnotationClient {
   
       @Refer(baseRefer = "baseRefer")
       private Hello hello;
   
       public static void main(String[] args) {
           ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("client_annotation.xml");
           appCtx.start();
           AnnotationClient client = appCtx.getBean(AnnotationClient.class);
           System.out.println(client.hello.hello());
       }
   }

   ```
   注解的方式同样支持同步调用和异步调用两种方式，只要在Refer注解里指定callback属性为MethodInvokeCallBack实现的全限定性名即可。Refer和Service注解里的属性与xml配置的属性一一对应。
   
## SpringBoot例子

1. 配置yml或properties文件，配置一个即可。

    `src/main/resources/application.yml`

    ```xml
    eagle:
      #扫描eagle服务，多个包用逗号分隔
      base-package: eagle.jfaster.org
    
      #注册中心配置，可以配置多个
      registry:
        - name: regCenter
          protocol: zookeeper
          address: 127.0.0.1:4181
          namespace: eagle
          base-sleep-time-milliseconds: 1000
          max-sleep-time-milliseconds: 3000
          max-retries: 3
    
      #协议配置，可以配置多个
      protocol:
        - id: proto
          name: eagle
          serialization: kryo
          use-default: true
          max-content-length: 16777216
          max-server-connection: 20000
          core-worker-thread: 20
          max-worker-thread: 400
          worker-queue-size: 10
          cluster: eagle
    
      #baseRefer配置，可以配置多个
      base-refer:
        - id: baseRefer
          request-timeout: 300
          actives: 20000
          actives-wait: 300
          loadbalance: roundrobin
          ha-strategy: failfast
          compress: false
          group: eagleGroup
          connect-timeout: 10000
          protocol: proto
          registry: regCenter
    
      #baseService配置，可以配置多个
      base-service:
        - id: baseService
          group: eagleGroup
          export: proto:9200
          registry: regCenter
    ```

    `src/main/resources/application.properties`

    ```xml
      #扫描eagle服务，多个包用逗号分隔
      eagle.base-package=eagle.jfaster.org
      
      #注册中心配置，可以配置多个
      eagle.registry[0].name=regCenter
      eagle.registry[0].protocol=zookeeper
      eagle.registry[0].address=127.0.0.1:4181
      eagle.registry[0].namespace=eagle
      eagle.registry[0].base-sleep-time-milliseconds=1000
      eagle.registry[0].max-sleep-time-milliseconds=3000
      eagle.registry[0].max-retries=3
      
      #协议配置，可以配置多个
      eagle.protocol[0].id=proto
      eagle.protocol[0].name=eagle
      eagle.protocol[0].serialization=kryo
      eagle.protocol[0].use-default=true
      eagle.protocol[0].max-content-length=16777216
      eagle.protocol[0].max-server-connection=20000
      eagle.protocol[0].core-worker-thread=20
      eagle.protocol[0].max-worker-thread=400
      eagle.protocol[0].worker-queue-size=10
      eagle.protocol[0].cluster=eagle
      
      #baseRefer配置，可以配置多个
      eagle.base-refer[0].id=baseRefer
      eagle.base-refer[0].request-timeout=300
      eagle.base-refer[0].actives=20000
      eagle.base-refer[0].actives-wait=300
      eagle.base-refer[0].loadbalance=roundrobin
      eagle.base-refer[0].ha-strategy=failfast
      eagle.base-refer[0].compress=false
      eagle.base-refer[0].group=eagleGroup
      eagle.base-refer[0].connect-timeout=10000
      eagle.base-refer[0].protocol=proto
      eagle.base-refer[0].registry=regCenter
      
      #baseService配置，可以配置多个
      eagle.base-service[0].id=baseService
      eagle.base-service[0].group=eagleGroup
      eagle.base-service[0].export=proto:9200
      eagle.base-service[0].registry=regCenter 
    ```
    
    

2. 创建接口

    `src/main/java/eagle/jfaster/org/service/Calculate.java`

    ```java
    
    package eagle.jfaster.org.service;
        
    /**
     * Created by fangyanpeng1 on 2017/8/9.
     */
    public interface Calculate {
    
        int add(int a, int b);
    
        int sub(int a, int b);
    }
    
    ```
3. 实现接口，并打上Service注解。
    
    `src/main/java/eagle/jfaster/org/service/impl/CalculateImpl.java`
    
    ```java
    package eagle.jfaster.org.service.impl;
    
    import eagle.jfaster.org.config.annotation.Service;
    import eagle.jfaster.org.service.Calculate;
    
    /**
     * Created by fangyanpeng1 on 2017/8/9.
     */
    @Service(id = "calculateService",baseService = "baseService",export = "proto:29001")
    public class CalculateImpl implements Calculate {
    
        public int add(int a, int b) {
            return a+b;
        }
    
        public int sub(int a, int b) {
            return a-b;
        }
    }

    ```
4. 启动服务端和客户端

    `src/main/java/eagle/jfaster/org/service/Calculator.java`
    
    ```java
        package eagle.jfaster.org.service;
        
        import eagle.jfaster.org.config.annotation.Refer;
        import org.springframework.stereotype.Service;
        
        /**
         * Created by fangyanpeng on 2017/10/24.
         */
        @Service
        public class Calculator {
        
            @Refer(baseRefer = "baseRefer")
            public Calculate calculate;
        
        }
    ```
    
    `src/main/java/eagle/jfaster/org/SpringBootSartup.java`

    ```java
        package eagle.jfaster.org;
        
        import eagle.jfaster.org.service.Calculator;
        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.context.ApplicationContext;
        
        import java.util.concurrent.TimeUnit;
        
        /**
         * Created by fangyanpeng1 on 2017/8/11.
         */
        @SpringBootApplication
        public class SpringBootSartup {
        
            public static void main(String[] args) throws InterruptedException {
                //启动Curator框架提供的内置zookeeper 仅供测试使用，生产环境请使用真实zookeeper地址
                EmbedZookeeperServer.start(4181);
                ApplicationContext ctx =  SpringApplication.run(SpringBootSartup.class, args);
                TimeUnit.SECONDS.sleep(2);
                Calculator calculator = ctx.getBean(Calculator.class);
                System.out.println(calculator.calculate.add(1,2));
                System.out.println(calculator.calculate.sub(9,5));
            }
        }
    ```
     SpringBoot方式同样支持同步调用和异步调用两种方式，只要在Refer注解里指定callback属性为MethodInvokeCallBack实现的全限定性名即可。Refer和Service注解里的属性与xml配置的属性一一对应。
     注意此例子中，由于Refer和Service在同一个工程，所以运行main方法Refer和Service就都启动了，实际生产环境中一般都是服务的调用和服务的实现部署在不同的进程中。

# eagle常用配置

## 注册中心的配置（eagle:registry）
   * name: 注册中心的名称，如果没有配置id，会用name当id。
   * protocol: 注册中心协议，目前只支持zookeeper。
   * address: 注册中心地址，如果是多个地址以逗号分隔，如果是多组用|或;分隔。
   * namespace: zk上的命名空间，所有的信息都在该命名空间下。
   * max-retries: 连接注册中心的重试次数。
   * base-sleep-time-milliseconds: 重试时间间隔。
   * max-sleep-time-milliseconds: 最大重试时间。
   * session-timeout-milliseconds: 与注册中心的会话超时时间。
   * digest: 连接注册中心的密码。

## 协议配置 (eagle:protocol）
   * name: 协议名称,目前只支持eagle，后续加入thrift，如果没有配置id，name会充当id。
   * serialization: 序列化，支持hessian、kryo、protobuf序列化协议。
   * heartbeat-factory: 心跳工厂,默认值是eagle,通过spi方式可以自定义心跳工厂。
   * select-thread-size: netty处理io的线程数，尽量不要阻塞netty的io线程。
   * core-worker-thread: 处理业务的核心线程数。
   * max-worker-thread:  处理业务的最大线程数。
   * max-content-length: rpc调用最大传输的字节数。
   * max-server-connection: 一个端口支持的最大连接数。
   * protect-strategy: 服务端负载保护策略，当服务端接收了过多的请求并且业务处理不过来时，进行负载保护。目前支持none、concurrent、memory 3种过载保护策略。none是默认策略，什么都不做。concurrent是并发保护策略，当并发达到max-worker-thread*3／4时，并且处理业务缓慢则拒绝接收新的请求。memory 是内存使用策略，当jvm内存使用超过90%时，拒绝接收新的请求。
   * codec: 用于编码和解码的工具类，默认调用EagleCodec，可以通过spi的方式自定义codec。
   * use-native: 在linux环境下，是否开启epoll。默认是true。

## 客户端配置（eagle:refer）
   * group: 调用组，客户端和服务端配置要一致。
   * version: 版本号，区分相同服务的不同版本，客户端与服务端的版本号一致才能调用成功。
   * retries: 调用失败重试次数。
   * actives: 支持的最大并发数。
   * actives-wait: 并发达到最大后等待多长时间。
   * check: 启动时是否检测有服务，默认false。
   * registry: 注册中心，多个注册中心以逗号分隔。
   * host: ip地址，一般不需要指定，系统会自动获取，如果特殊需求可自己设定。
   * request-timeout: 请求超时时间。
   * min-client-connection: 最小连接数。
   * max-client-connection: 最大连接数。
   * idle-time: 连接空闲多长时间会被回收。
   * connect-timeout: 获取连接的超时时间。
   * max-invoke-error: 连续调用失败的的次数，超过这个次数，这个服务设置为不可用。
   * compress: 是否开启gzip压缩。
   * loadbalance: 负载均衡策略，目前支持random（随机）、roundrobin（轮询）、activeWeigth（以调用量小的优先）、weight（根据配置的权重选择）。
   * ha-strategy: ha策略，目前支持failover、failfast。
   * interface: 服务的接口。
   * callback: 回调，如果设置了回调，该服务就会变成异步。
   * callback-thread: 回调执行线程池的大小。
   * callback-queue-size: 回调任务队列大小。
   * callback-wait-time: 回调任务执行等待时间与request-timeout不同,如果异步任务队列超过设定阈值并且任务等待时间过长，则将此服务设置为不可用，直到任务队列在合理范围内。
   * base-refer: 公共的refer配置。
   * stats-log: 统计log的名称，如果配置了该名称，则会把接口方法的调用时间、tps等信息写入此log，方便查看接口的性能。
   * mock: 接口失败降级类的全限定名。如果配置了mock，接口调用失败，就会降级调用mock的实现。mock需要实现 eagle.jfaster.org.rpc.Mock接口。
   

## 服务端配置（eagle:service）
   * group: 调用组，客户端和服务端配置要一致。
   * version: 版本号，区分相同服务的不同版本，客户端与服务端的版本号一致才能调用成功。
   * registry: 注册中心，多个注册中心以逗号分隔。
   * host: ip地址，一般不需要指定，系统会自动获取，如果特殊需求可自己设定。
   * interface: 服务的接口。
   * base-service: 公共的service配置。
   * ref: 接口的实现类引用。
   * class: 如果没有配置ref，会根据class加载接口实现类。
   * export: 服务暴露的协议和端口号，多个用逗号分割，如proto:7000,proto:8000，proto是协议的id。
   * weight: 权重，与权重负载均衡算法联合使用。
   * service-type: 服务调用类型，支持jdk和cglib两种配置，默认是jdk。如果是jdk，采用反射机制调用实现类的方法；如果是cglib，采用cglib索引机制直接调用实现类的方法，性能更好。
   
# 后台管理界面
   > eagle 提供可视化的后台管理，方便查看和修改配置。
   > 启动后台的步骤
   * tar -zxvf eagle-ui-1.4.tar.gz
   * cd eagle-ui-1.4
   * vim conf/eagle.conf 修改用户名、密码、jvm参数、日志路径、端口号等
   * sh bin/eagle.sh start
   
       ![Image text](https://raw.githubusercontent.com/fang-yan-peng/eagle/master/eagle-ui.jpeg)
 
   
# 贡献者

* fangyanpeng([@fangyanpeng](https://github.com/fang-yan-peng))

[maven]:https://maven.apache.org
[gradle]:http://gradle.org
[zookeeper]:http://zookeeper.apache.org
[kryo]:https://github.com/EsotericSoftware/kryo
[hessian]:http://hessian.caucho.com/


