package eagle.jfaster.org.repository.impl;


import eagle.jfaster.org.pojo.GlobalConfiguration;
import eagle.jfaster.org.repository.ConfigurationsXmlRepository;

/**
 * 基于XML的全局配置数据访问器实现类.
 *
 * @author fangyanpeng
 */
public final class ConfigurationsXmlRepositoryImpl extends AbstractXmlRepositoryImpl<GlobalConfiguration> implements ConfigurationsXmlRepository {
    
    public ConfigurationsXmlRepositoryImpl() {
        super("Configurations.xml", GlobalConfiguration.class);
    }
}
