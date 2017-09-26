package eagle.jfaster.org.rpc.support;

import com.google.common.collect.Maps;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.util.ExceptionUtil;
import eagle.jfaster.org.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 远端调用实现
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
public class EagleRpcRemoteInvoke<T> implements RemoteInvoke<T> {

    private InternalLogger logger = InternalLoggerFactory.getInstance(EagleRpcRemoteInvoke.class);

    private final Class<T> interfaceClz;

    private final T invokeImpl;

    private final MergeConfig config;

    private final Map<String,Method> methodInvoke = Maps.newHashMap();

    public EagleRpcRemoteInvoke(Class<T> interfaceClz, T invokeImpl, MergeConfig config) {
        this.interfaceClz = interfaceClz;
        this.invokeImpl = invokeImpl;
        this.config = config;
        initMethodInvoke();
    }

    @Override
    public Response invoke(Request request) {
        String methodDesc = ReflectUtil.getMethodDesc(request.getMethodName(), request.getParameterDesc());
        Method method = methodInvoke.get(methodDesc);
        EagleResponse response = new EagleResponse();
        if(method == null){
            response.setException(new EagleFrameException("Error - invoke method '%s' is not exist",methodDesc));
            return response;
        }
        try {
            Object value = method.invoke(invokeImpl,request.getParameters());
            response.setValue(value);
        } catch (Throwable e) {
            logger.error("EagleRpcRemoteInvoke invoke error",e);
            response.setException(new EagleFrameException(ExceptionUtil.transform(e)));
        }
        return response;
    }

    @Override
    public MergeConfig getConfig() {
        return config;
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClz;
    }

    private void initMethodInvoke() {
        Method[] methods = interfaceClz.getMethods();
        for (Method method : methods) {
            String methodDesc = ReflectUtil.getMethodDesc(method);
            methodInvoke.put(methodDesc, method);
        }
    }
}
