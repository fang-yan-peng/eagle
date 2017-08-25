
package eagle.jfaster.org.service;
import com.google.common.base.Optional;
import eagle.jfaster.org.pojo.RegistryCenterConfiguration;
import eagle.jfaster.org.pojo.RegistryCenterConfigurations;

/**
 * 注册中心配置服务.
 *
 * @author fangyanpeng
 */
public interface RegistryCenterConfigurationService {
    
    /**
     * 读取全部注册中心配置.
     *
     * @return 全部注册中心配置
     */
    RegistryCenterConfigurations loadAll();
    
    /**
     * 读取注册中心配置.
     *
     * @param name 配置名称
     * @return 注册中心配置
     */
    RegistryCenterConfiguration load(String name);
    
    /**
     * 查找注册中心配置.
     * 
     * @param name 配置名称
     * @param configs 全部注册中心配置
     * @return 注册中心配置
     */
    RegistryCenterConfiguration find(final String name, final RegistryCenterConfigurations configs);
    
    /**
     * 读取已连接的注册中心配置.
     *
     * @return 已连接的注册中心配置
     */
    Optional<RegistryCenterConfiguration> loadActivated();
    
    /**
     * 添加注册中心配置.
     *
     * @param config 注册中心配置
     * @return 是否添加成功
     */
    boolean add(RegistryCenterConfiguration config);
    
    /**
     * 删除注册中心配置.
     *
     * @param name 配置名称
     */
    void delete(String name);
}
