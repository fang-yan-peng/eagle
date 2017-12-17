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
package eagle.jfaster.org.bean.trace;

import com.google.common.base.Strings;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.support.OpaqueGenerator;
import eagle.jfaster.org.rpc.support.TraceContext;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.transform.impl.UndeclaredThrowableStrategy;
import org.springframework.core.SmartClassLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

/**
 * Created by fangyanpeng on 2017/12/16.
 */

public class EagleTraceCglibProxy implements AopProxy, Serializable {

    // Constants for CGLIB callback array indices
    private static final int AOP_PROXY = 0;
    private static final int INVOKE_TARGET = 1;
    private static final int NO_OVERRIDE = 2;
    private static final int DISPATCH_TARGET = 3;
    private static final int DISPATCH_ADVISED = 4;
    private static final int INVOKE_EQUALS = 5;
    private static final int INVOKE_HASHCODE = 6;


    protected final static InternalLogger logger = InternalLoggerFactory.getInstance(EagleTraceCglibProxy.class);

    private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap<Class<?>, Boolean>();

    protected final AdvisedSupport advised;

    private Object[] constructorArgs;

    private Class<?>[] constructorArgTypes;

    private final transient EagleTraceCglibProxy.AdvisedDispatcher advisedDispatcher;

    private transient Map<String, Integer> fixedInterceptorMap;

    private transient int fixedInterceptorOffset;

    public EagleTraceCglibProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
        this.advisedDispatcher = new EagleTraceCglibProxy.AdvisedDispatcher(this.advised);
    }

    public void setConstructorArguments(Object[] constructorArgs, Class<?>[] constructorArgTypes) {
        if (constructorArgs == null || constructorArgTypes == null) {
            throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
        }
        if (constructorArgs.length != constructorArgTypes.length) {
            throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length +
                    ") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
        }
        this.constructorArgs = constructorArgs;
        this.constructorArgTypes = constructorArgTypes;
    }


    @Override
    public Object getProxy() {
        return getProxy(null);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating CGLIB proxy: target source is " + this.advised.getTargetSource());
        }

        try {
            Class<?> rootClass = this.advised.getTargetClass();
            Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

            Class<?> proxySuperClass = rootClass;
            if (ClassUtils.isCglibProxyClass(rootClass)) {
                proxySuperClass = rootClass.getSuperclass();
                Class<?>[] additionalInterfaces = rootClass.getInterfaces();
                for (Class<?> additionalInterface : additionalInterfaces) {
                    this.advised.addInterface(additionalInterface);
                }
            }

            // Validate the class, writing log messages as necessary.
            validateClassIfNecessary(proxySuperClass, classLoader);

            // Configure CGLIB Enhancer...
            Enhancer enhancer = createEnhancer();
            if (classLoader != null) {
                enhancer.setClassLoader(classLoader);
                if (classLoader instanceof SmartClassLoader &&
                        ((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
                    enhancer.setUseCache(false);
                }
            }
            enhancer.setSuperclass(proxySuperClass);
            enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setStrategy(new UndeclaredThrowableStrategy(UndeclaredThrowableException.class));

            Callback[] callbacks = getCallbacks();
            Class<?>[] types = new Class<?>[callbacks.length];
            for (int x = 0; x < types.length; x++) {
                types[x] = callbacks[x].getClass();
            }
            // fixedInterceptorMap only populated at this point, after getCallbacks call above
            enhancer.setCallbackFilter(new EagleTraceCglibProxy.ProxyCallbackFilter(this.advised, this.fixedInterceptorMap, this.fixedInterceptorOffset));
            enhancer.setCallbackTypes(types);

            // Generate the proxy class and create a proxy instance.
            return createProxyClassAndInstance(enhancer, callbacks);
        }
        catch (CodeGenerationException ex) {
            throw new AopConfigException("Could not generate CGLIB subclass of class [" +
                    this.advised.getTargetClass() + "]: " +
                    "Common causes of this problem include using a final class or a non-visible class",
                    ex);
        }
        catch (IllegalArgumentException ex) {
            throw new AopConfigException("Could not generate CGLIB subclass of class [" +
                    this.advised.getTargetClass() + "]: " +
                    "Common causes of this problem include using a final class or a non-visible class",
                    ex);
        }
        catch (Exception ex) {
            // TargetSource.getTarget() failed
            throw new AopConfigException("Unexpected AOP exception", ex);
        }
    }

    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setCallbacks(callbacks);
        return (this.constructorArgs != null ?
                enhancer.create(this.constructorArgTypes, this.constructorArgs) :
                enhancer.create());
    }


    protected Enhancer createEnhancer() {
        return new Enhancer();
    }


    private void validateClassIfNecessary(Class<?> proxySuperClass, ClassLoader proxyClassLoader) {
        if (logger.isInfoEnabled()) {
            synchronized (validatedClasses) {
                if (!validatedClasses.containsKey(proxySuperClass)) {
                    doValidateClass(proxySuperClass, proxyClassLoader);
                    validatedClasses.put(proxySuperClass, Boolean.TRUE);
                }
            }
        }
    }


    private void doValidateClass(Class<?> proxySuperClass, ClassLoader proxyClassLoader) {
        if (!Object.class.equals(proxySuperClass)) {
            Method[] methods = proxySuperClass.getDeclaredMethods();
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (!Modifier.isStatic(mod)) {
                    if (Modifier.isFinal(mod)) {
                        logger.info("Unable to proxy method [" + method + "] because it is final: " +
                                "All calls to this method via a proxy will NOT be routed to the target instance.");
                    }
                    else if (!Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(mod) &&
                            proxyClassLoader != null && proxySuperClass.getClassLoader() != proxyClassLoader) {
                        logger.info("Unable to proxy method [" + method + "] because it is package-visible " +
                                "across different ClassLoaders: All calls to this method via a proxy will " +
                                "NOT be routed to the target instance.");
                    }
                }
            }
            doValidateClass(proxySuperClass.getSuperclass(), proxyClassLoader);
        }
    }

    private Callback[] getCallbacks() throws Exception {

        // Choose an "aop" interceptor (used for AOP calls).
        Callback aopInterceptor = new EagleTraceCglibProxy.DynamicAdvisedInterceptor(this.advised);

        // Choose a "straight to target" interceptor. (used for calls that are
        // unadvised but can return this). May be required to expose the proxy.
        Callback targetInterceptor = new EagleTraceCglibProxy.DynamicUnadvisedInterceptor(this.advised.getTargetSource());


        // Choose a "direct to target" dispatcher (used for
        // unadvised calls to static targets that cannot return this).
        Callback targetDispatcher = new EagleTraceCglibProxy.SerializableNoOp();

        Callback[] callbacks = new Callback[]{
                aopInterceptor, // for normal advice
                targetInterceptor, // invoke target without considering advice, if optimized
                new EagleTraceCglibProxy.SerializableNoOp(), // no override for methods mapped to this
                targetDispatcher, this.advisedDispatcher,
                new EagleTraceCglibProxy.EqualsInterceptor(this.advised),
                new EagleTraceCglibProxy.HashCodeInterceptor(this.advised)
        };


        return callbacks;
    }

    /**
     * Process a return value. Wraps a return of {@code this} if necessary to be the
     * {@code proxy} and also verifies that {@code null} is not returned as a primitive.
     */
    private static Object processReturnType(Object proxy, Object target, Method method, Object retVal) {
        // Massage return value if necessary
        if (retVal != null && retVal == target && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            // Special case: it returned "this". Note that we can't help
            // if the target sets a reference to itself in another returned object.
            retVal = proxy;
        }
        Class<?> returnType = method.getReturnType();
        if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException(
                    "Null return value from advice does not match primitive return type for: " + method);
        }
        return retVal;
    }


    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof EagleTraceCglibProxy &&
                AopProxyUtils.equalsInProxy(this.advised, ((EagleTraceCglibProxy) other).advised)));
    }

    @Override
    public int hashCode() {
        return EagleTraceCglibProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }


    /**
     * Serializable replacement for CGLIB's NoOp interface.
     * Public to allow use elsewhere in the framework.
     */
    public static class SerializableNoOp implements NoOp, Serializable {
    }


    /**
     * Interceptor used to invoke a dynamic target without creating a method
     * invocation or evaluating an advice chain. (We know there was no advice
     * for this method.)
     */
    private static class DynamicUnadvisedInterceptor implements MethodInterceptor, Serializable {

        private final TargetSource targetSource;

        public DynamicUnadvisedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object target = this.targetSource.getTarget();
            try {
                Object retVal = methodProxy.invoke(target, args);
                return processReturnType(proxy, target, method, retVal);
            }
            finally {
                this.targetSource.releaseTarget(target);
            }
        }
    }

    /**
     * Dispatcher for any methods declared on the Advised class.
     */
    private static class AdvisedDispatcher implements Dispatcher, Serializable {

        private final AdvisedSupport advised;

        public AdvisedDispatcher(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object loadObject() throws Exception {
            return this.advised;
        }
    }


    /**
     * Dispatcher for the {@code equals} method.
     * Ensures that the method call is always handled by this class.
     */
    private static class EqualsInterceptor implements MethodInterceptor, Serializable {

        private final AdvisedSupport advised;

        public EqualsInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            Object other = args[0];
            if (proxy == other) {
                return true;
            }
            if (other instanceof Factory) {
                Callback callback = ((Factory) other).getCallback(INVOKE_EQUALS);
                if (!(callback instanceof EagleTraceCglibProxy.EqualsInterceptor)) {
                    return false;
                }
                AdvisedSupport otherAdvised = ((EagleTraceCglibProxy.EqualsInterceptor) callback).advised;
                return AopProxyUtils.equalsInProxy(this.advised, otherAdvised);
            }
            else {
                return false;
            }
        }
    }


    /**
     * Dispatcher for the {@code hashCode} method.
     * Ensures that the method call is always handled by this class.
     */
    private static class HashCodeInterceptor implements MethodInterceptor, Serializable {

        private final AdvisedSupport advised;

        public HashCodeInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            return EagleTraceCglibProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
        }
    }

    /**
     * General purpose AOP callback. Used when the target is dynamic or when the
     * proxy is not frozen.
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {

        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Class<?> targetClass = null;
            Object target = null;
            try {
                // May be null. Get as late as possible to minimize the time we
                // "own" the target, in case it comes from a pool...
                target = getTarget();
                if (target != null) {
                    targetClass = target.getClass();
                }
                Object retVal;
                if(EagleTraceMethodRecods.needTrace(method,targetClass)){
                    boolean clear = Strings.isNullOrEmpty(TraceContext.getOpaque());
                    try {
                        if(clear){
                            TraceContext.setOpaque(OpaqueGenerator.getDistributeOpaque());
                        }
                        retVal = methodProxy.invoke(target, args);
                    } catch (Throwable e){
                        logger.error("Eagle trace error: ",e);
                        throw new EagleFrameException(e);
                    } finally {
                        if(clear){
                            TraceContext.clear();
                        }
                    }
                }else {
                    retVal = methodProxy.invoke(target, args);
                }
                retVal = processReturnType(proxy, target, method, retVal);
                return retVal;
            }
            finally {
                if (target != null) {
                    releaseTarget(target);
                }
            }
        }

        @Override
        public boolean equals(Object other) {
            return (this == other ||
                    (other instanceof EagleTraceCglibProxy.DynamicAdvisedInterceptor &&
                            this.advised.equals(((EagleTraceCglibProxy.DynamicAdvisedInterceptor) other).advised)));
        }

        /**
         * CGLIB uses this to drive proxy creation.
         */
        @Override
        public int hashCode() {
            return this.advised.hashCode();
        }

        protected Object getTarget() throws Exception {
            return this.advised.getTargetSource().getTarget();
        }

        protected void releaseTarget(Object target) throws Exception {
            this.advised.getTargetSource().releaseTarget(target);
        }
    }

    private static class ProxyCallbackFilter implements CallbackFilter {

        private final AdvisedSupport advised;

        private final Map<String, Integer> fixedInterceptorMap;

        private final int fixedInterceptorOffset;

        public ProxyCallbackFilter(AdvisedSupport advised, Map<String, Integer> fixedInterceptorMap, int fixedInterceptorOffset) {
            this.advised = advised;
            this.fixedInterceptorMap = fixedInterceptorMap;
            this.fixedInterceptorOffset = fixedInterceptorOffset;
        }

        @Override
        public int accept(Method method) {
            if (AopUtils.isFinalizeMethod(method)) {
                logger.debug("Found finalize() method - using NO_OVERRIDE");
                return NO_OVERRIDE;
            }
            if (!this.advised.isOpaque() && method.getDeclaringClass().isInterface() &&
                    method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Method is declared on Advised interface: " + method);
                }
                return DISPATCH_ADVISED;
            }
            // We must always proxy equals, to direct calls to this.
            if (AopUtils.isEqualsMethod(method)) {
                logger.debug("Found 'equals' method: " + method);
                return INVOKE_EQUALS;
            }
            // We must always calculate hashCode based on the proxy.
            if (AopUtils.isHashCodeMethod(method)) {
                logger.debug("Found 'hashCode' method: " + method);
                return INVOKE_HASHCODE;
            }
            Class<?> targetClass = this.advised.getTargetClass();
            // Proxy is not yet available, but that shouldn't matter.
            List<?> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            boolean haveAdvice = !chain.isEmpty();
            boolean exposeProxy = this.advised.isExposeProxy();
            boolean isStatic = this.advised.getTargetSource().isStatic();
            boolean isFrozen = this.advised.isFrozen();
            if (haveAdvice || !isFrozen) {
                // If exposing the proxy, then AOP_PROXY must be used.
                if (exposeProxy) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Must expose proxy on advised method: " + method);
                    }
                    return AOP_PROXY;
                }
                String key = method.toString();
                // Check to see if we have fixed interceptor to serve this method.
                // Else use the AOP_PROXY.
                if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(key)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Method has advice and optimisations are enabled: " + method);
                    }
                    // We know that we are optimising so we can use the
                    // FixedStaticChainInterceptors.
                    int index = this.fixedInterceptorMap.get(key);
                    return (index + this.fixedInterceptorOffset);
                }
                else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Unable to apply any optimisations to advised method: " + method);
                    }
                    return AOP_PROXY;
                }
            }
            else {
                // See if the return type of the method is outside the class hierarchy
                // of the target type. If so we know it never needs to have return type
                // massage and can use a dispatcher.
                // If the proxy is being exposed, then must use the interceptor the
                // correct one is already configured. If the target is not static, then
                // cannot use a dispatcher because the target cannot be released.
                if (exposeProxy || !isStatic) {
                    return INVOKE_TARGET;
                }
                Class<?> returnType = method.getReturnType();
                if (targetClass == returnType) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Method " + method +
                                "has return type same as target type (may return this) - using INVOKE_TARGET");
                    }
                    return INVOKE_TARGET;
                }
                else if (returnType.isPrimitive() || !returnType.isAssignableFrom(targetClass)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Method " + method +
                                " has return type that ensures this cannot be returned- using DISPATCH_TARGET");
                    }
                    return DISPATCH_TARGET;
                }
                else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Method " + method +
                                "has return type that is assignable from the target type (may return this) - " +
                                "using INVOKE_TARGET");
                    }
                    return INVOKE_TARGET;
                }
            }
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof EagleTraceCglibProxy.ProxyCallbackFilter)) {
                return false;
            }
            EagleTraceCglibProxy.ProxyCallbackFilter otherCallbackFilter = (EagleTraceCglibProxy.ProxyCallbackFilter) other;
            AdvisedSupport otherAdvised = otherCallbackFilter.advised;
            if (this.advised == null || otherAdvised == null) {
                return false;
            }
            if (this.advised.isFrozen() != otherAdvised.isFrozen()) {
                return false;
            }
            if (this.advised.isExposeProxy() != otherAdvised.isExposeProxy()) {
                return false;
            }
            if (this.advised.getTargetSource().isStatic() != otherAdvised.getTargetSource().isStatic()) {
                return false;
            }
            if (!AopProxyUtils.equalsProxiedInterfaces(this.advised, otherAdvised)) {
                return false;
            }
            // Advice instance identity is unimportant to the proxy class:
            // All that matters is type and ordering.
            Advisor[] thisAdvisors = this.advised.getAdvisors();
            Advisor[] thatAdvisors = otherAdvised.getAdvisors();
            if (thisAdvisors.length != thatAdvisors.length) {
                return false;
            }
            for (int i = 0; i < thisAdvisors.length; i++) {
                Advisor thisAdvisor = thisAdvisors[i];
                Advisor thatAdvisor = thatAdvisors[i];
                if (!equalsAdviceClasses(thisAdvisor, thatAdvisor)) {
                    return false;
                }
                if (!equalsPointcuts(thisAdvisor, thatAdvisor)) {
                    return false;
                }
            }
            return true;
        }

        private boolean equalsAdviceClasses(Advisor a, Advisor b) {
            Advice aa = a.getAdvice();
            Advice ba = b.getAdvice();
            if (aa == null || ba == null) {
                return (aa == ba);
            }
            return aa.getClass().equals(ba.getClass());
        }

        private boolean equalsPointcuts(Advisor a, Advisor b) {
            // If only one of the advisor (but not both) is PointcutAdvisor, then it is a mismatch.
            // Takes care of the situations where an IntroductionAdvisor is used (see SPR-3959).
            return (!(a instanceof PointcutAdvisor) ||
                    (b instanceof PointcutAdvisor &&
                            ObjectUtils.nullSafeEquals(((PointcutAdvisor) a).getPointcut(), ((PointcutAdvisor) b).getPointcut())));
        }

        @Override
        public int hashCode() {
            int hashCode = 0;
            Advisor[] advisors = this.advised.getAdvisors();
            for (Advisor advisor : advisors) {
                Advice advice = advisor.getAdvice();
                if (advice != null) {
                    hashCode = 13 * hashCode + advice.getClass().hashCode();
                }
            }
            hashCode = 13 * hashCode + (this.advised.isFrozen() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isExposeProxy() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isOptimize() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isOpaque() ? 1 : 0);
            return hashCode;
        }
    }

}

