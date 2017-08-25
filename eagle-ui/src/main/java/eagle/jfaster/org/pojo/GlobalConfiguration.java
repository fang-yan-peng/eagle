package eagle.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 全局配置.
 *
 * @author fangyanpeng
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class GlobalConfiguration {
    
    private RegistryCenterConfigurations registryCenterConfigurations;
    
}
