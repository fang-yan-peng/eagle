package eagle.jfaster.org.task;

import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by fangyanpeng on 2017/8/22.
 */

@RequiredArgsConstructor
public class TimeoutMonitorTask implements Runnable {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(TimeoutMonitorTask.class);

    private final NettyClient client;

    @Override
    public void run() {
        Iterator<Map.Entry<Integer, NettyResponseFuture>> it = client.getCallbackMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, NettyResponseFuture> next = it.next();
            NettyResponseFuture rep = next.getValue();
            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 300) <= System.currentTimeMillis()) {
                if(rep.getCallBack() == null){
                    rep.onFail(new EagleFrameException("%s request timeout，requestid:%d,timeout:%d ms", client.getConfig().getInterfaceName(),rep.getOpaque(),rep.getTimeoutMillis()));
                }else {
                    rep.setException(new EagleFrameException("%s request timeout，requestid:%d,timeout:%d ms", client.getConfig().getInterfaceName(),rep.getOpaque(),rep.getTimeoutMillis()));
                    client.executeInvokeCallback(rep);
                }
                it.remove();
                logger.warn("remove timeout request, interfaceName: " + client.getConfig().getInterfaceName());
            }
        }
    }
}
