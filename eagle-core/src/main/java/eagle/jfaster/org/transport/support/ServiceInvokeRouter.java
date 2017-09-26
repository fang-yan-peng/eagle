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

package eagle.jfaster.org.transport.support;

import com.google.common.collect.Maps;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.ProtectStrategy;
import eagle.jfaster.org.rpc.support.EagleResponse;
import eagle.jfaster.org.rpc.RemoteInvoke;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.spi.SpiClassLoader;
import eagle.jfaster.org.transport.InvokeRouter;
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

    private ProtectStrategy protectStrategy;

    public ServiceInvokeRouter(RemoteInvoke invoke) {
        MergeConfig config = invoke.getConfig();
        String strategyName = config.getExt(ConfigEnum.protectStrategy.getName(),ConfigEnum.protectStrategy.getValue());
        protectStrategy = SpiClassLoader.getClassLoader(ProtectStrategy.class).getExtension(strategyName);
        if(protectStrategy == null){
            throw new EagleFrameException("Error protect strategy name %s not exists",strategyName);
        }
        addRemoteInvoke(invoke);
    }

    @Override
    public Response routeAndInvoke(Request message) {
        String serviceKey = message.getInterfaceName();
        RemoteInvoke invoker = services.get(serviceKey);
        if(invoker == null){
            logger.info(String.format("Error invoke service %s not exist ",serviceKey));
            EagleResponse response = new EagleResponse();
            response.setException(new EagleFrameException("Error invoke service %s not exist",serviceKey));
            return response;
        }
        try {
            return protectStrategy.protect(message,invoker,methodCnt.get());
        } catch (Throwable e) {
            logger.error(String.format("Invoke '%s' error: ",message.getInterfaceName()),e);
            EagleResponse response = new EagleResponse();
            response.setException(new EagleFrameException(e.getMessage()));
            return response;
        }
    }


    @Override
    public void addRemoteInvoke(RemoteInvoke invoke){
        String serviceKey = invoke.getConfig().getInterfaceName();
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
