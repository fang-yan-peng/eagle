package eagle.jfaster.org.bean.trace;

import com.google.common.base.Strings;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.support.OpaqueGenerator;
import eagle.jfaster.org.rpc.support.TraceContext;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.interceptor.MethodCacheKey;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fangyanpeng on 2017/12/16.
 */
public class EagleTraceJdkProxy implements AopProxy, InvocationHandler, Serializable {

    private static final long serialVersionUID = 5531744639992436466L;

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EagleTraceJdkProxy.class);

    private final AdvisedSupport advised;

    private boolean equalsDefined;

    private boolean hashCodeDefined;

    private final ConcurrentHashMap<MethodCacheKey,Boolean> traces = new ConcurrentHashMap<>();

    public EagleTraceJdkProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
    }


    @Override
    public Object getProxy() {
        return getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating JDK dynamic proxy: target source is " + this.advised.getTargetSource());
        }
        Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised);
        findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }

    private void findDefinedEqualsAndHashCodeMethods(Class<?>[] proxiedInterfaces) {
        for (Class<?> proxiedInterface : proxiedInterfaces) {
            Method[] methods = proxiedInterface.getDeclaredMethods();
            for (Method method : methods) {
                if (AopUtils.isEqualsMethod(method)) {
                    this.equalsDefined = true;
                }
                if (AopUtils.isHashCodeMethod(method)) {
                    this.hashCodeDefined = true;
                }
                if (this.equalsDefined && this.hashCodeDefined) {
                    return;
                }
            }
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TargetSource targetSource = this.advised.getTargetSource();
        Class<?> targetClass = null;
        Object target = null;

        try {
            if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
                // The target does not implement the equals(Object) method itself.
                return equals(args[0]);
            }
            if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
                // The target does not implement the hashCode() method itself.
                return hashCode();
            }
            if (method.getDeclaringClass().isInterface() &&
                    method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                // Service invocations on ProxyConfig with the proxy config...
                return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
            }

            Object retVal;

            // May be null. Get as late as possible to minimize the time we "own" the target,
            // in case it comes from a pool.
            target = targetSource.getTarget();
            if (target != null) {
                targetClass = target.getClass();
            }

            if(EagleTraceMethodRecods.needTrace(method,targetClass)){
                boolean clear = Strings.isNullOrEmpty(TraceContext.getTraceId());
                try {
                    if(clear){
                        TraceContext.setTraceId(OpaqueGenerator.getDistributeOpaque());
                    }
                    retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
                } catch (Throwable e){
                    logger.error("Eagle trace error:  ",e);
                    throw new EagleFrameException(e);
                } finally {
                    if(clear){
                        TraceContext.clear();
                    }
                }

            }else {
                retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
            }

            // Massage return value if necessary.
            Class<?> returnType = method.getReturnType();
            if (retVal != null && retVal == target && returnType.isInstance(proxy) &&
                    !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
                retVal = proxy;
            }
            else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
                throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
            }
            return retVal;
        }
        finally {
            if (target != null && !targetSource.isStatic()) {
                // Must have come from TargetSource.
                targetSource.releaseTarget(target);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }

        EagleTraceJdkProxy otherProxy;
        if (other instanceof EagleTraceJdkProxy) {
            otherProxy = (EagleTraceJdkProxy) other;
        }
        else if (Proxy.isProxyClass(other.getClass())) {
            InvocationHandler ih = Proxy.getInvocationHandler(other);
            if (!(ih instanceof EagleTraceJdkProxy)) {
                return false;
            }
            otherProxy = (EagleTraceJdkProxy) ih;
        }
        else {
            // Not a valid comparison...
            return false;
        }

        // If we get here, otherProxy is the other AopProxy.
        return AopProxyUtils.equalsInProxy(this.advised, otherProxy.advised);
    }


    @Override
    public int hashCode() {
        return EagleTraceJdkProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }

}
