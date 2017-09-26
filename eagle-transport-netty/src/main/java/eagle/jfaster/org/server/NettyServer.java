/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package eagle.jfaster.org.server;

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
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.statistic.EagleStatsManager;
import eagle.jfaster.org.statistic.StatisticCallback;
import eagle.jfaster.org.transport.InvokeRouter;
import eagle.jfaster.org.transport.Server;
import eagle.jfaster.org.transport.StandardThreadExecutor;
import eagle.jfaster.org.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.RequiredArgsConstructor;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一个端口对应一个netty server
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@RequiredArgsConstructor
public class NettyServer implements Server,StatisticCallback {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyServer.class);


    private final MergeConfig config;

    private final InvokeRouter<Request,Response> invokeRouter;

    private NettyConnectManage connectManage;

    private StandardThreadExecutor standardThreadExecutor;

    private AtomicBoolean init = new AtomicBoolean(false);

    private ServerBootstrap bootstrap;

    private EventLoopGroup groupSelector;

    private EventLoopGroup groupAccept;

    @Override
    public void start() {
        if(init.compareAndSet(false,true)){
            try {
                initServer();
                bootstrap.bind(new InetSocketAddress(config.getPort())).sync();
                EagleStatsManager.registerStatsCallback(this);
            } catch (Throwable e) {
                logger.error("Error start server ",e);
                throw new EagleFrameException(e.getMessage());
            }
        }
    }

    @Override
    public void shutdown() {
        if(init.compareAndSet(true,false)){
            try {
                groupAccept.shutdownGracefully();
                groupSelector.shutdownGracefully();
                connectManage.close();
                standardThreadExecutor.shutdownNow();
            } catch (Exception e) {
                logger.error("Error shutdown server",e);
            }
        }
    }

    private void initServer(){
        final int maxContentLen = config.getExtInt(ConfigEnum.maxContentLength.getName(),ConfigEnum.maxContentLength.getIntValue());
        int maxConnection = config.getExtInt(ConfigEnum.maxServerConnection.getName(),ConfigEnum.maxServerConnection.getIntValue());
        int coreWorker = config.getExtInt(ConfigEnum.coreWorkerThread.getName(),ConfigEnum.coreWorkerThread.getIntValue());
        int maxWorker = config.getExtInt(ConfigEnum.maxWorkerThread.getName(),ConfigEnum.maxWorkerThread.getIntValue());
        int workerQueueSize = config.getExtInt(ConfigEnum.workerQueueSize.getName(),ConfigEnum.workerQueueSize.getIntValue());
        int selectWorker = config.getExtInt(ConfigEnum.selectThreadSize.getName(),ConfigEnum.selectThreadSize.getIntValue());
        standardThreadExecutor = new StandardThreadExecutor(coreWorker, maxWorker, workerQueueSize, new DefaultThreadFactory("NettyServer-" + config.hostPort(), true));
        standardThreadExecutor.prestartAllCoreThreads();
        connectManage = new NettyConnectManage(maxConnection);
        boolean useNative = RemotingUtil.isLinuxPlatform() && config.getExtBoolean(ConfigEnum.useNative.getName(),ConfigEnum.useNative.isBooleanValue());
        if(useNative){
            groupAccept = new EpollEventLoopGroup(1,new DefaultThreadFactory("NettyServer-accept-" + config.hostPort(), true));
            groupSelector = new EpollEventLoopGroup(selectWorker,new DefaultThreadFactory("NettyServer-select-" + config.hostPort(), true));
        }else {
            groupAccept = new NioEventLoopGroup(1,new DefaultThreadFactory("NettyServer-accept-" + config.hostPort(), true));
            groupSelector =  new NioEventLoopGroup(selectWorker,new DefaultThreadFactory("NettyServer-select-" + config.hostPort(), true));
        }
        final Codec codec = SpiClassLoader.getClassLoader(Codec.class).getExtension(config.getExt(ConfigEnum.codec.getName(),ConfigEnum.codec.getValue()));
        final Serialization serialization = SpiClassLoader.getClassLoader(Serialization.class).getExtension(config.getExt(ConfigEnum.serialization.getName(),ConfigEnum.serialization.getValue()));
        bootstrap = new ServerBootstrap();
        bootstrap.group(groupAccept,groupSelector).channel(useNative ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,false)
                .option(ChannelOption.SO_REUSEADDR,true)
                .option(ChannelOption.SO_BACKLOG,10240)
                .option(ChannelOption.SO_SNDBUF, SOCKET_SNDBUF_SIZE)
                .option(ChannelOption.SO_RCVBUF,SOCKET_RCVBUF_SIZE)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sch) throws Exception {
                        sch.pipeline().addLast(connectManage)
                                .addLast(new NettyEncoder(codec,serialization))
                                .addLast(new NettyDecorder(maxContentLen,codec,serialization))
                                .addLast(new MessageChannelHandler(invokeRouter,standardThreadExecutor));
                    }
                });
    }

    @Override
    public String statistic() {
        int maxWorkerThread = config.getExtInt(ConfigEnum.maxWorkerThread.getName(),ConfigEnum.maxWorkerThread.getIntValue());
        //当活跃线程数达到最大线程数的50%时才打印server的统计信息
        if(standardThreadExecutor.getActiveCount() < maxWorkerThread * 1/2){
            return null;
        }
        return String.format(
                "[%s://%s] connectionCount: %s taskCount: %s queueCount: %s maxThreadCount: %s maxTaskCount: %s",
                config.getProtocol(),config.hostPort(), connectManage.getChannels().size(), standardThreadExecutor.getSubmittedTasksCount(),
                standardThreadExecutor.getQueue().size(), standardThreadExecutor.getMaximumPoolSize(),
                standardThreadExecutor.getMaxSubmittedTaskCount());
    }
}
