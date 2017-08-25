package eagle.jfaster.org.controller;

import com.google.common.base.Optional;
import eagle.jfaster.org.pojo.RegistryCenterConfiguration;
import eagle.jfaster.org.factory.RegistryCenterFactory;
import eagle.jfaster.org.service.RegistryCenterConfigurationService;
import eagle.jfaster.org.service.impl.RegistryCenterConfigurationServiceImpl;
import eagle.jfaster.org.util.SessionRegistryCenterConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * 注册中心配置的RESTful API.
 *
 * @author fangyanpeng
 */
@RestController
@RequestMapping("/registry-center")
public final class RegistryCenterRestfulApi {
    
    public static final String REG_CENTER_CONFIG_KEY = "reg_center_config_key";
    
    private RegistryCenterConfigurationService regCenterService = new RegistryCenterConfigurationServiceImpl();
    
    /**
     * 判断是否存在已连接的注册中心配置.
     *
     * @param request HTTP请求
     * @return 是否存在已连接的注册中心配置
     */
    @RequestMapping(value = "/activated",method = RequestMethod.GET)
    public boolean activated(final HttpServletRequest request) {
        return regCenterService.loadActivated().isPresent();
    }
    
    /**
     * 读取注册中心配置集合.
     * 
     * @param request HTTP请求
     * @return 注册中心配置集合
     */
    @RequestMapping(value = "/load",method = RequestMethod.GET)
    public Collection<RegistryCenterConfiguration> load(final HttpServletRequest request) {
        Optional<RegistryCenterConfiguration> regCenterConfig = regCenterService.loadActivated();
        if (regCenterConfig.isPresent()) {
            setRegistryCenterNameToSession(regCenterConfig.get(), request.getSession());
        }
        return regCenterService.loadAll().getRegistryCenterConfiguration();
    }
    
    /**
     * 添加注册中心.
     * 
     * @param config 注册中心配置
     * @return 是否添加成功
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public boolean add(final @RequestBody RegistryCenterConfiguration config) {
        return regCenterService.add(config);
    }
    
    /**
     * 删除注册中心.
     *
     * @param config 注册中心配置
     */
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    public boolean delete(final @RequestBody  RegistryCenterConfiguration config) {
        regCenterService.delete(config.getName());
        return true;
    }
    
    @RequestMapping(value = "/connect",method = RequestMethod.POST)
    public boolean connect(final @RequestBody RegistryCenterConfiguration config, final HttpServletRequest request) {
        boolean isConnected = setRegistryCenterNameToSession(regCenterService.find(config.getName(), regCenterService.loadAll()), request.getSession());
        if (isConnected) {
            regCenterService.load(config.getName());
        }
        return isConnected;
    }
    
    private boolean setRegistryCenterNameToSession(final RegistryCenterConfiguration regCenterConfig, final HttpSession session) {
        session.setAttribute(REG_CENTER_CONFIG_KEY, regCenterConfig);
        try {
            RegistryCenterFactory.createCoordinatorRegistryCenter(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
            SessionRegistryCenterConfiguration.setRegistryCenterConfiguration((RegistryCenterConfiguration) session.getAttribute(REG_CENTER_CONFIG_KEY));
        } catch (final Exception ex) {
            return false;
        }
        return true;
    }
}
