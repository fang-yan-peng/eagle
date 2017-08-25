package eagle.jfaster.org.service.impl;

import com.google.common.base.Optional;
import eagle.jfaster.org.factory.ServiceApiFactory;
import eagle.jfaster.org.pojo.RegistryCenterConfiguration;
import eagle.jfaster.org.service.InterfaceApiService;
import eagle.jfaster.org.service.InterfaceService;
import eagle.jfaster.org.util.SessionRegistryCenterConfiguration;
import org.springframework.stereotype.Service;

/**
 * @author fangyanpeng
 */
@Service
public class InterfaceServiceImpl implements InterfaceService {

    @Override
    public InterfaceApiService getApiService() {
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        return ServiceApiFactory.createServiceAPI(regCenterConfig.getZkAddressList(),regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
    }
}
