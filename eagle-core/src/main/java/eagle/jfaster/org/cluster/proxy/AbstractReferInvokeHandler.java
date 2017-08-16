package eagle.jfaster.org.cluster.proxy;

import eagle.jfaster.org.cluster.ReferCluster;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.support.EagleRequest;
import eagle.jfaster.org.rpc.support.OpaqueGenerator;
import eagle.jfaster.org.util.ReflectUtil;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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

    public AbstractReferInvokeHandler(List<ReferCluster<T>> clusters, Class<T> clz) {
        this.clusters = clusters;
        this.clz = clz;
        this.interfaceName = clz.getName();
        selectDefaultCluster();
        compress = defaultCluster.getConfig().getExtBoolean(ConfigEnum.compress.getName(),ConfigEnum.compress.isBooleanValue());
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
        EagleRequest request = new EagleRequest();
        request.setInterfaceName(interfaceName);
        request.setOpaque(OpaqueGenerator.getOpaque());
        request.setParameters(args);
        request.setMethodName(method.getName());
        request.setNeedCompress(compress);
        request.setParameterDesc(ReflectUtil.getMethodParamDesc(method));
        request.setParameterRuntimeDesc(ReflectUtil.getMethodParamDesc(args));
        try {
            return handle(method,request);
        } catch (Exception e) {
            ReferCluster<T> tmp = this.defaultCluster;
            selectDefaultCluster();
            if(tmp != defaultCluster){
                logger.info(String.format("ReferInvokeHandler.invoke,interface:%s,from %s to %s",interfaceName,tmp.getConfig().identity(),defaultCluster.getConfig().identity()));
                try {
                    return handle(method,request);
                } catch (Exception e1) {
                    logger.error(String.format("ReferInvokeHandler.invoke,interface:%s,protocol:%s",interfaceName,defaultCluster.getConfig().identity()),e1);
                    throw e1;
                }
            }else {
                logger.error(String.format("ReferInvokeHandler.invoke,interface:%s,protocol:%s",interfaceName,defaultCluster.getConfig().identity()),e);
                throw e;
            }

        }
    }

    protected abstract Object handle(Method method,Request request);
}
