package eagle.jfaster.org.factory;

import com.google.common.base.Optional;
import eagle.jfaster.org.service.InterfaceApiService;
import eagle.jfaster.org.service.impl.InterfaceApiServiceImpl;

/**
 * @author fangyanpeng
 */
public class ServiceApiFactory {

    /**
     * 创建服务管理API对象.
     *
     * @param connectString 注册中心连接字符串
     * @param namespace 注册中心命名空间
     * @param digest 注册中心凭证
     * @return 服务管理API对象
     */
    public static InterfaceApiService createServiceAPI(final String connectString, final String namespace, final Optional<String> digest) {
        return new InterfaceApiServiceImpl(RegistryCenterFactory.createCoordinatorRegistryCenter(connectString, namespace, digest));
    }
}
