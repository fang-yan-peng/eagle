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

package eagle.jfaster.org.cluster.proxy;

import com.google.common.base.Strings;
import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.support.EagleRequest;
import eagle.jfaster.org.rpc.support.OpaqueGenerator;
import eagle.jfaster.org.rpc.support.TraceContext;
import eagle.jfaster.org.util.ReflectUtil;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 *
 *
 * Created by fangyanpeng1 on 2017/8/6.
 */
public abstract class AbstractReferInvokeHandler<T> implements InvocationHandler {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(AbstractReferInvokeHandler.class);

    private List<ReferCluster<T>> clusters;

    protected volatile ReferCluster<T> defaultCluster;

    private Class<T> clz;

    private boolean compress;

    private String interfaceName;

    private static final Object[] NO_ARGS = {};

    public AbstractReferInvokeHandler(List<ReferCluster<T>> clusters, Class<T> clz) {
        this.clusters = clusters;
        this.clz = clz;
        this.interfaceName = clz.getName();
        selectDefaultCluster();
        this.compress = defaultCluster.getConfig().getExtBoolean(ConfigEnum.compress.getName(),ConfigEnum.compress.isBooleanValue());
    }

    private void selectDefaultCluster(){
        for(ReferCluster<T> cluster : clusters){
            if(cluster.getConfig().getExtBoolean(ConfigEnum.useDefault.getName(),ConfigEnum.useDefault.isBooleanValue())){
                if(this.defaultCluster != cluster){
                    defaultCluster = cluster;
                    break;
                }
            }
        }
        this.defaultCluster = (defaultCluster == null ? clusters.get(0) : defaultCluster);
        logger.info(String.format("Interface:%s to use protocol:%s",interfaceName,defaultCluster.getConfig().identity()));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args == null) {
            args = NO_ARGS;
        }
        if (args.length == 0 && method.getName().equals("hashCode")) {
            return hashCode();
        }
        if (args.length == 1 && method.getName().equals("equals") && method.getParameterTypes()[0] == Object.class) {
            Object arg = args[0];
            return proxy.getClass().isInstance(arg) && equals(Proxy.getInvocationHandler(arg));
        }
        if (args.length == 0 && method.getName().equals("toString")) {
            return toString();
        }
        EagleRequest request = new EagleRequest();
        request.setInterfaceName(interfaceName);
        String traceId = TraceContext.getTraceId();
        if(!Strings.isNullOrEmpty(traceId)){
            request.setAttachment(TraceContext.TRACE_KEY,traceId);
        }
        request.setOpaque(OpaqueGenerator.getOpaque());
        request.setParameters(args);
        request.setMethodName(method.getName());
        request.setNeedCompress(compress);
        request.setParameterDesc(ReflectUtil.getMethodParamDesc(method));
        try {
            return handle(method,request);
        } catch (Throwable e) {
            ReferCluster<T> tmp = this.defaultCluster;
            selectDefaultCluster();
            if(tmp != defaultCluster){
                logger.info(String.format("ReferInvokeHandler invoke,interface: '%s',from '%s' to '%s'",interfaceName,tmp.getConfig().identity(),defaultCluster.getConfig().identity()));
                try {
                    return handle(method,request);
                } catch (Throwable e1) {
                    logger.error(String.format("ReferInvokeHandler invoke,interface: '%s',protocol: '%s'",interfaceName,defaultCluster.getConfig().identity()),e1);
                    throw e1;
                }
            }else {
                logger.error(String.format("ReferInvokeHandler invoke,interface: '%s',protocol: '%s'",interfaceName,defaultCluster.getConfig().identity()),e);
                throw e;
            }

        }
    }

    protected abstract Object handle(Method method,Request request);
}
