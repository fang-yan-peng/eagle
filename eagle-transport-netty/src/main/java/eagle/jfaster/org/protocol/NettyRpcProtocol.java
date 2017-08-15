package eagle.jfaster.org.protocol;

import com.google.common.collect.Maps;
import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.pool.SuspendResumeLock;
import eagle.jfaster.org.rpc.*;
import eagle.jfaster.org.rpc.support.EagleRpcRemoteInvoke;
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
            router.addRemoteInvoke(new EagleRpcRemoteInvoke(HeartBeat.class,heartBeat,heartBeat.getConfig()));
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
        SuspendResumeLock lock;
        //并发控制
        int actives = config.getExtInt(ConfigEnum.actives.getName(),ConfigEnum.actives.getIntValue());
        if(actives == 0){
            lock = SuspendResumeLock.FAUX_LOCK;
        }else {
            long activesWait = config.getExtLong(ConfigEnum.activesWait.getName(),ConfigEnum.activesWait.getLongValue());
            lock = new SuspendResumeLock(actives,activesWait);
        }
        Refer<T> refer = new NettyRefer<>(client,config,type,lock);
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
            host2Server.clear();
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
