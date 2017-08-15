/*
package eagle.jfaster.com;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;
import org.apache.thrift.TException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;

*/
/**
 * Created by fangyanpeng1 on 2017/7/7.
 *//*

public class Server {
    public static void main(String[] args) {
        List<ThriftEventHandler> handlers = new ArrayList<>();
        handlers.add(new ThriftEventHandler() {
            @Override
            public void preRead(Object context, String methodName) throws TException {
                System.out.println(methodName);
                System.out.println(context==null);
            }

            @Override
            public void postRead(Object context, String methodName, Object[] args) throws TException {
                System.out.println(methodName);
                System.out.println(context == null);
                System.out.println(args.length);
            }

            @Override public void preWrite(Object context, String methodName, Object result) throws TException {
                System.out.println(result);
            }
        });
        ThriftServiceProcessor processor = new ThriftServiceProcessor(
                new ThriftCodecManager(),
                handlers,
                new ScribeImpl(),new Scribe1Impl()
        );

        ExecutorService taskWorkerExecutor = newFixedThreadPool(1);

        ThriftServerDef serverDef = ThriftServerDef.newBuilder()
                .listen(8899)
                .withProcessor(processor)
                .using(taskWorkerExecutor)
                .build();

        ExecutorService bossExecutor = newCachedThreadPool();
        ExecutorService ioWorkerExecutor = newCachedThreadPool();

        NettyServerConfig serverConfig = NettyServerConfig.newBuilder()
                .setBossThreadExecutor(bossExecutor)
                .setWorkerThreadExecutor(ioWorkerExecutor)
                .build();

        ThriftServer server = new ThriftServer(serverConfig, serverDef);
        server.start();
    }
}
*/
