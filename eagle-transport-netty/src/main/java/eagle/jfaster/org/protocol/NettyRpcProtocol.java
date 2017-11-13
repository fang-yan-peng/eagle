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

package eagle.jfaster.org.protocol;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.pool.SuspendResumeLock;
import eagle.jfaster.org.rpc.*;
import eagle.jfaster.org.rpc.support.EagleRpcJdkRemoteInvoke;
import eagle.jfaster.org.server.NettyServer;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.transport.*;
import eagle.jfaster.org.transport.support.ServiceInvokeRouter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * 把相应的协议暴露出去,相同的协议可以暴露在多个不同的端口
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@SpiInfo(name = "eagle")
public class NettyRpcProtocol<T> implements Protocol<T> {

    private Map<String,InvokeRouter<Request,Response>> host2Router = Maps.newHashMap();

    private Map<String,Server> host2Server = Maps.newHashMap();

    private AtomicBoolean alive = new AtomicBoolean(true);

    @Override
    public Exporter createServer(RemoteInvoke invoker) {
        MergeConfig config = invoker.getConfig();
        InvokeRouter<Request,Response> router = getInvokeRouter(invoker);
        Server nettyServer;
        if(router.isExport()){
            //添加心跳响应
            HeartBeatFactory heartBeatFactory = SpiClassLoader.getClassLoader(HeartBeatFactory.class).getExtension(config.getExt(ConfigEnum.heartbeatFactory.getName(),ConfigEnum.heartbeatFactory.getValue()));
            if(heartBeatFactory == null){
                throw new EagleFrameException("HeartBeatFactory not exist,name: %s",config.getExt(ConfigEnum.heartbeatFactory.getName(),""));
            }
            HeartBeat heartBeat = heartBeatFactory.createHeartBeat(config.copy());
            router.addRemoteInvoke(new EagleRpcJdkRemoteInvoke(HeartBeat.class,heartBeat,heartBeat.getConfig()));
            //
            nettyServer = new NettyServer(config,router);
            nettyServer.start();
            host2Server.put(config.hostPort(),nettyServer);
        }else {
            nettyServer = host2Server.get(config.hostPort());
        }
        Exporter exporter =  new NettyRpcExporter(invoker,nettyServer);
        exporter.init();
        return exporter;
    }

    @Override
    public Refer createRefer(MergeConfig config,Class<T> type) {
        Client client = new NettyClient(config,config.getInvokeCallBack());
        SuspendResumeLock lock = SuspendResumeLock.FAUX_LOCK;
        //并发控制
        int actives = config.getExtInt(ConfigEnum.actives.getName(),ConfigEnum.actives.getIntValue());
        if(actives != 0){
            long activesWait = config.getExtLong(ConfigEnum.activesWait.getName(),ConfigEnum.activesWait.getLongValue());
            lock = new SuspendResumeLock(actives,activesWait);
        }
        //是否统计调用信息，如果配置了统计日志则统计各个方法的调用信息
        String  logName= config.getExt(ConfigEnum.statsLog.getName(),ConfigEnum.statsLog.getValue());
        Refer<T> refer = Strings.isNullOrEmpty(logName) ? new NettyRefer<>(client,config,type,lock) : new StatsNettyRefer<>(client,config,type,lock,InternalLoggerFactory.getInstance(logName));
        refer.init();
        return refer;
    }

    @Override
    public void close() {
        if(alive.compareAndSet(true,false)){
            for (Map.Entry<String,Server> entry : host2Server.entrySet()){
                entry.getValue().shutdown();
            }
            host2Server.clear();
            host2Router.clear();
        }
    }

    @Override
    public boolean isOpen() {
        return alive.get();
    }

    private synchronized InvokeRouter<Request,Response> getInvokeRouter(RemoteInvoke<T> invoke){
        String hostInfo = invoke.getConfig().hostPort();
        InvokeRouter<Request,Response> router = host2Router.get(hostInfo);
        if(router != null){
            router.addRemoteInvoke(invoke);
        }else {
            router = new ServiceInvokeRouter(invoke);
            host2Router.put(hostInfo,router);
        }
        return router;
    }
}
