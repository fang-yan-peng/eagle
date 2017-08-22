package eagle.jfaster.org.client;

import eagle.jfaster.org.client.channel.AbstractNettyChannel;
import eagle.jfaster.org.client.channel.AsyncNettyChannel;
import eagle.jfaster.org.client.channel.SyncNettyChannel;
import eagle.jfaster.org.client.handler.AsyncMessageHandler;
import eagle.jfaster.org.client.handler.SyncMessageHandler;
import eagle.jfaster.org.client.pool.NettySharedConnPool;
import eagle.jfaster.org.codec.Codec;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.coder.NettyDecorder;
import eagle.jfaster.org.coder.NettyEncoder;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import static eagle.jfaster.org.constant.EagleConstants.*;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.MethodInvokeCallBack;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.ResponseFuture;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.statistic.EagleStatsManager;
import eagle.jfaster.org.statistic.StatisticCallback;
import eagle.jfaster.org.task.AsyncCallbackMonitor;
import eagle.jfaster.org.task.AsyncCallbackTask;
import eagle.jfaster.org.task.TimeoutMonitorTask;
import eagle.jfaster.org.thread.AsyncCallbackExecutor;
import eagle.jfaster.org.transport.Client;
import eagle.jfaster.org.util.RemotingUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * netty 客户端
 *
 * Created by fangyanpeng1 on 2017/8/1.
 */
@RequiredArgsConstructor
public class NettyClient implements Client,StatisticCallback {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyClient.class);

    private final MergeConfig config;

    @Getter
    private final MethodInvokeCallBack callBack;

    private EventLoopGroup workerGroup;

    @Getter
    private Bootstrap bootstrap;

    private AtomicBoolean stat = new AtomicBoolean(false);

    @Setter
    private volatile boolean suspend = false;

    private AtomicBoolean init = new AtomicBoolean(false);

    // 连续失败次数
    private AtomicLong errorCount = new AtomicLong(0);

    @Getter
    private Map<Integer,NettyResponseFuture> callbackMap = new ConcurrentHashMap(256);

    private ScheduledFuture<?> callbackCancelFuture = null;

    // 回收过期任务
    private static ScheduledExecutorService callbackCancelExecutor = Executors.newScheduledThreadPool(4);

    //检测异步任务执行超时
    private static ScheduledExecutorService callbackMonitorExecutor;

    // 回调执行线程池
    private ThreadPoolExecutor callbackExecutor;

    @Getter
    private BlockingQueue<Runnable> callbackQueue;

    //最大连续失败次数
    private int maxInvokeError = 0;

    private InetSocketAddress remoteAddress;

    private NettySharedConnPool connPool;

    @Override
    public void start() {
        try {
            if(init.compareAndSet(false,true)){
                remoteAddress = new InetSocketAddress(config.getHost(),config.getPort());
                maxInvokeError = config.getExtInt(ConfigEnum.maxInvokeError.getName(),ConfigEnum.maxInvokeError.getIntValue());
                if(callBack != null){
                    int callbackWorkerThread = config.getExtInt(ConfigEnum.callbackThread.getName(),ConfigEnum.callbackThread.getIntValue());
                    int callbackQueueSize = config.getExtInt(ConfigEnum.callbackQueueSize.getName(),ConfigEnum.callbackQueueSize.getIntValue());
                    callbackQueue = new LinkedBlockingQueue<>(callbackQueueSize);
                    callbackExecutor = new AsyncCallbackExecutor(callbackWorkerThread,callbackWorkerThread,10*60*1000,TimeUnit.MILLISECONDS, callbackQueue, new DefaultThreadFactory("Method callback exec-"+config.getInterfaceName()+"-"));
                    callbackExecutor.allowCoreThreadTimeOut(true);
                    if(callbackMonitorExecutor == null){
                        callbackMonitorExecutor = Executors.newScheduledThreadPool(1);
                    }
                    callbackMonitorExecutor.scheduleWithFixedDelay(new AsyncCallbackMonitor(this),ASYNC_TIMEOUT_TIMER_PERIOD,ASYNC_TIMEOUT_TIMER_PERIOD,TimeUnit.MILLISECONDS);
                }
                boolean useNative = RemotingUtil.isLinuxPlatform() && config.getExtBoolean(ConfigEnum.useNative.getName(),ConfigEnum.useNative.isBooleanValue());
                if(useNative){
                    workerGroup = new EpollEventLoopGroup(GRUOUP_WORKER_THREAD,new DefaultThreadFactory("Method invoke exec-"+config.getInterfaceName()+"-"));
                }else {
                    workerGroup = new NioEventLoopGroup(GRUOUP_WORKER_THREAD,new DefaultThreadFactory("Method invoke exec-"+config.getInterfaceName()+"-"));
                }
                bootstrap = new Bootstrap();
                final int maxContentLen = config.getExtInt(ConfigEnum.maxContentLength.getName(),ConfigEnum.maxContentLength.getIntValue());
                final Codec codec = SpiClassLoader.getClassLoader(Codec.class).getExtension(config.getExt(ConfigEnum.codec.getName(),ConfigEnum.codec.getValue()));
                final Serialization serialization = SpiClassLoader.getClassLoader(Serialization.class).getExtension(config.getExt(ConfigEnum.serialize.getName(),ConfigEnum.serialize.getValue()));
                bootstrap.group(workerGroup).channel(useNative ? EpollSocketChannel.class : NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY,true)
                        .option(ChannelOption.SO_KEEPALIVE,false)
                        .option(ChannelOption.SO_SNDBUF,SOCKET_SNDBUF_SIZE)
                        .option(ChannelOption.SO_RCVBUF,SOCKET_RCVBUF_SIZE)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel sc) throws Exception {
                                sc.pipeline().addLast(new NettyEncoder(codec,serialization))
                                        .addLast(new NettyDecorder(maxContentLen,codec,serialization))
                                        .addLast(new IdleStateHandler(0,0,config.getExtInt(ConfigEnum.heartbeat.getName(),ConfigEnum.heartbeat.getIntValue())))
                                        .addLast(new NettyConnectionManager(config,connPool,NettyClient.this))
                                        .addLast(NettyClient.this.callBack == null ? new SyncMessageHandler(NettyClient.this) : new AsyncMessageHandler(NettyClient.this));
                            }
                        });
                //定时扫描超时的请求
                callbackCancelFuture = callbackCancelExecutor.scheduleWithFixedDelay(new TimeoutMonitorTask(this),NETTY_TIMEOUT_TIMER_PERIOD,NETTY_TIMEOUT_TIMER_PERIOD, TimeUnit.MICROSECONDS);
                connPool = new NettySharedConnPool(config,this);
                EagleStatsManager.egisterStatsCallback(this);
                stat.set(true);

            }
        } catch (Exception e) {
            logger.error("Error start netty client ",e);
            throw new EagleFrameException(e);
        }
    }

    //只有单线程执行该方法，不用加同步
    public AbstractNettyChannel newChannel() throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(remoteAddress).sync();
        Channel channel = channelFuture.channel();
        return this.callBack == null ? new SyncNettyChannel(this,channel) : new AsyncNettyChannel(this,channel);
    }

    public NettyResponseFuture removeCallBack(Integer opaque){
        return callbackMap.remove(opaque);
    }

    public void addCallBack(Integer opaque,NettyResponseFuture future){
        callbackMap.put(opaque,future);
    }

    public void executeInvokeCallback(final ResponseFuture responseFuture) {
        boolean runInThisThread = false;
        if (callbackExecutor != null) {
            try {
                callbackExecutor.submit(new AsyncCallbackTask(responseFuture));
            } catch (Exception e) {
                runInThisThread = true;
                logger.info("execute callback in executor exception, maybe executor busy", e);
            }
        } else {
            runInThisThread = true;
        }
        if (runInThisThread) {
            try {
                responseFuture.executeCallback();
            } catch (Throwable e) {
                logger.info("executeInvokeCallback Exception", e);
            }
        }
    }

    @Override
    public MergeConfig getConfig() {
        return config;
    }

    @Override
    public boolean isAlive() {
        return stat.get() && !suspend;
    }


    @Override
    public void shutdown() {
        try {
            if(init.compareAndSet(true,false)){
                workerGroup.shutdownGracefully();
                //不能关闭callBackCancleExecutor，因为是多个client公用的。
                callbackCancelFuture.cancel(true);
                if(callbackExecutor != null){
                    callbackExecutor.shutdownNow();
                }
                if(callbackMonitorExecutor != null){
                    callbackMonitorExecutor.shutdownNow();
                }
                callbackMap.clear();
                connPool.shutdown();
                logger.info("Netty client normal shutdown");
            }
        } catch (Exception e) {
            logger.error("Netty client shutdown ",e);
        }
    }

    @Override
    public Object request(Request request) {
        AbstractNettyChannel channel = null;
        try {
            channel = connPool.getConnection();
            return channel.request(request);
        } catch (Exception e) {
            logger.error("NettyClient request error,interface:"+config.getInterfaceName()+",host:"+config.identity(),e);
            if(channel != null){
                connPool.invalidateConnection(channel);
            }
            throw new EagleFrameException(e.getMessage());
        }finally {
            if(channel != null){
                connPool.release(channel);
            }
        }
    }

    /**
     * 联系调用失败超过配置的次数，将该client设置为无效
     */
    public void incrErrorCount() {
        long count = errorCount.incrementAndGet();
        // 如果节点是可用状态，同时当前连续失败的次数超过限制maxClientConnection次，那么把该节点标示为不可用
        if (count >= maxInvokeError && stat.get()) {
            if(stat.compareAndSet(true,false)){
                logger.error("NettyClient unavailable Error: config=" + config.getInterfaceName() + " " + config.identity());
            }
        }
    }

    /**
     * 通过心跳机制检测如果client恢复，则重新启用client
     */
    public void resetErrorCount() {
        errorCount.set(0);
        if(!stat.get() && init.get() && errorCount.intValue() < maxInvokeError && stat.compareAndSet(false,true)){
            logger.info("NettyClient recover available: interfaceName=" + config.getInterfaceName() + " " + config.identity());
        }
    }

    @Override
    public String statistic() {
        return String.format("identity:%s callbackMapSize:%d asyncCallbackQueueSize:%d",config.identity(), callbackMap.size(), callbackQueue == null ? 0 : callbackQueue.size());
    }

}
