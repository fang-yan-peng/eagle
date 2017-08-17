package eagle.jfaster.org.transport.support;

import com.google.common.collect.Maps;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.support.EagleResponse;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.transport.InvokeRouter;
import eagle.jfaster.org.util.ExploreUtil;
import eagle.jfaster.org.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个端口对应一个路由
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */

public class ServiceInvokeRouter implements InvokeRouter<Request,Response> {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ServiceInvokeRouter.class);

    private final Map<String,RemoteInvoke<?>> services = Maps.newHashMap();

    private AtomicBoolean export = new AtomicBoolean(false);

    private AtomicInteger methodCnt = new AtomicInteger(0);

    public ServiceInvokeRouter(RemoteInvoke invoke) {
        addRemoteInvoke(invoke);
    }

    @Override
    public Response routeAndInvoke(Request message) {
        String serviceKey = ExploreUtil.getServiceKey(message);
        RemoteInvoke invoker = services.get(serviceKey);
        if(invoker == null){
            logger.info("Error invoke service {} not exist ",serviceKey);
            EagleResponse response = new EagleResponse();
            response.setException(new EagleFrameException("Error invoke service %s not exist",serviceKey));
            return response;
        }
        try {
            if(isAllow(message,invoker)){
                return invoker.invoke(message);
            }else {
                EagleResponse response = new EagleResponse();
                response.setException(new EagleFrameException("Not allow invoke service %s because of too many invoke at the same time",serviceKey));
                return response;
            }
        } catch (Exception e) {
            EagleResponse response = new EagleResponse();
            response.setException(new EagleFrameException("Error invoke service %s",e.getMessage()));
            return response;
        }
    }

    protected boolean isAllow(Request request,RemoteInvoke invoker){
        return true;
    }

    @Override
    public void addRemoteInvoke(RemoteInvoke invoke){
        String serviceKey = ExploreUtil.getServiceKey(invoke.getConfig());
        if(services.containsKey(serviceKey)){
            throw new EagleFrameException("Error service %s has explored",serviceKey);
        }
        services.put(serviceKey,invoke);
        List<Method> methods = ReflectUtil.getPublicMethod(invoke.getInterface());
        methodCnt.addAndGet(methods.size());
    }

    @Override
    public boolean isExport(){
        return export.compareAndSet(false,true);
    }
}
