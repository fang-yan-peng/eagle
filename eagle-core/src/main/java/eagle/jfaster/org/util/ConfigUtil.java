package eagle.jfaster.org.util;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eagle.jfaster.org.config.*;
import eagle.jfaster.org.config.annotation.ConfigDesc;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.registry.RegistryCenter;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static eagle.jfaster.org.constant.EagleConstants.COMMA_SPLIT_PATTERN;
import static eagle.jfaster.org.constant.EagleConstants.HOST_SPLIT_PATTERN;
import static eagle.jfaster.org.constant.EagleConstants.REGISTRY_SPLIT_PATTERN;

/**
 * config提取和检查工具类
 *
 * Created by fangyanpeng1 on 2017/8/7.
 */
public class ConfigUtil {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ConfigUtil.class);


    private static LoadingCache<RegistryConfig,MergeConfig> regCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(
            new CacheLoader<RegistryConfig, MergeConfig>() {
                @Override
                public MergeConfig load(RegistryConfig regConfig) throws Exception {
                    String protocol = regConfig.getProtocol();
                    String address = regConfig.getAddress();
                    if(Strings.isNullOrEmpty(protocol) || Strings.isNullOrEmpty(address)){
                        return null;
                    }
                    String[] addrs = REGISTRY_SPLIT_PATTERN.split(address);
                    if(address == null || address.length() == 0){
                        return null;
                    }
                    for (String addr : addrs){
                        String[] singleAddrs = COMMA_SPLIT_PATTERN.split(addr);
                        Arrays.sort(singleAddrs);
                        String singleAddr = singleAddrs[0];
                        String[] hostInfo = HOST_SPLIT_PATTERN.split(singleAddr);
                        if(hostInfo == null || hostInfo.length != 2){
                            continue;
                        }
                        MergeConfig config = new MergeConfig();
                        config.setProtocol(protocol);
                        config.setInterfaceName(RegistryCenter.class.getName());
                        config.setVersion(ConfigEnum.version.getValue());
                        config.setHost(hostInfo[0]);
                        config.setPort(Integer.parseInt(hostInfo[1]));
                        collectConfigParams(config,regConfig);
                        config.addExt(ConfigEnum.address.getName(),addr);
                        return config;
                    }
                    return null;
                }
            });

    public static void collectConfigParams(MergeConfig data,AbstractConfig... configs) throws Exception {
        for(AbstractConfig config : configs){
            collectConfigParams(data,config);
        }
    }

    public static void collectMethodConfigParams(MergeConfig data, List<MethodConfig> methodConfigs) throws Exception {
        if (CollectionUtil.isEmpty(methodConfigs)) {
            return;
        }
        for (MethodConfig mc : methodConfigs) {
            if (mc != null) {
                collectConfigParams(data,mc,mc.getName()+"("+mc.getArgumentTypes()+")");
            }
        }
    }

    public static List<MergeConfig> loadRegistryConfigs(List<RegistryConfig> regConfigs) throws Exception {
        if(CollectionUtil.isEmpty(regConfigs)){
            return null;
        }
        List<MergeConfig> configs = new ArrayList<>(2);
        for(RegistryConfig regConfig : regConfigs){
            MergeConfig config = regCache.get(regConfig);
            if(config != null){
                configs.add(config);
            }
        }
        return configs;
    }

    public static void collectConfigParams(MergeConfig data,AbstractConfig config) throws Exception {
        collectConfigParams(data,config,null);
    }

    public static void collectConfigParams(MergeConfig data,AbstractConfig config,String prefix) throws Exception {
        try {
            if(config == null){
                return;
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(config.getClass());
            PropertyDescriptor[] pros = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pro : pros){
                Method rdM = pro.getReadMethod();
                if( rdM != null && isPrimitive(rdM.getReturnType())){
                    ConfigDesc configDesc = rdM.getAnnotation(ConfigDesc.class);
                    if (configDesc != null && configDesc.excluded()) {
                        continue;
                    }
                    String key = pro.getName();
                    if (configDesc != null && !Strings.isNullOrEmpty(configDesc.key())) {
                        key = configDesc.key();
                    }
                    Object value = rdM.invoke(config);
                    if(value == null || Strings.isNullOrEmpty(String.valueOf(value))){
                        if(configDesc != null && configDesc.required()){
                            throw new EagleFrameException("parameter:%s is not allow null or empty",key);
                        }
                        continue;
                    }
                    data.addExt(Strings.isNullOrEmpty(prefix) ? key : String.format("%s.%s",prefix,key),String.valueOf(value));
                }
            }
        } catch (Exception e) {
            logger.error("ConfigUtil.collectConfigParams",e);
            throw e;
        }
    }


    public static void checkInterfaceAndMethods(Class<?> interfaceClass, List<MethodConfig> methods) {
        if (interfaceClass == null) {
            throw new IllegalStateException("interface not allow null!");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        if (!CollectionUtil.isEmpty(methods)) {
            for (MethodConfig methodBean : methods) {
                String methodName = methodBean.getName();
                if (Strings.isNullOrEmpty(methodName)) {
                    throw new IllegalStateException("<eagle:method> name attribute is required! Please check: <eagle:service interface=\"" + interfaceClass.getName() + "\" ... ><eagle:method name=\"\" ... /></<eagle:service>");
                }
                java.lang.reflect.Method hasMethod = null;
                for (java.lang.reflect.Method method : interfaceClass.getMethods()) {
                    if (method.getName().equals(methodName)) {
                        if (methodBean.getArgumentTypes() != null && ReflectUtil.getMethodParamDesc(method).equals(methodBean.getArgumentTypes())) {
                            hasMethod = method;
                            break;
                        }
                        if (methodBean.getArgumentTypes() != null) {
                            continue;
                        }
                        if (hasMethod != null) {
                            throw new EagleFrameException("The interface:%s has more than one method:%s, must set argumentTypes attribute.",interfaceClass.getName(),methodName);
                        }
                        hasMethod = method;
                    }
                }
                if (hasMethod == null) {
                    throw new EagleFrameException("The interface:%s not found method:%s",interfaceClass.getName(),methodName);
                }
                methodBean.setArgumentTypes(ReflectUtil.getMethodParamDesc(hasMethod));
            }
        }
    }

    public static Set<ProAndPort> parseExport(String export) {
        if (Strings.isNullOrEmpty(export)) {
            return null;
        }
        Set<ProAndPort> protocol2Ports = new HashSet<>();
        String[] protocolAndPorts = COMMA_SPLIT_PATTERN.split(export);
        for (String protocolAndPort : protocolAndPorts) {
            if (Strings.isNullOrEmpty(protocolAndPort)) {
                continue;
            }
            String[] ppDetail = HOST_SPLIT_PATTERN.split(protocolAndPort);
            if (ppDetail.length == 2) {
                protocol2Ports.add(new ProAndPort(ppDetail[0], Integer.parseInt(ppDetail[1])));
            } else {
                throw new EagleFrameException("Export is malformed :%s" , export);
            }
        }
        return protocol2Ports;
    }

    public static String getLocalHostAddress(List<MergeConfig> regConfigs) {
        String localAddress = null;
        Map<String, Integer> regHostPorts = new HashMap<String, Integer>();
        for (MergeConfig ru : regConfigs) {
            regHostPorts.put(ru.getHost(), ru.getPort());
        }
        InetAddress address = NetUtil.getLocalAddress(regHostPorts);
        if (address != null) {
            localAddress = address.getHostAddress();
        }

        if (NetUtil.isValidLocalHost(localAddress)) {
            return localAddress;
        }
        throw new EagleFrameException("Please config local server hostname with intranet IP first!");
    }

    private static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || type == String.class || type == Character.class || type == Boolean.class || type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == Float.class || type == Double.class;
    }

    public static <M> List<M> check(List<M> regList,Map<String,M> regMap,String ex){
        if(!CollectionUtil.isEmpty(regList)){
           return regList;
        }
        if(!CollectionUtil.isEmpty(regMap)){
            return new ArrayList<>(regMap.values());
        }
        throw new EagleFrameException(ex);
    }
}
