package eagle.jfaster.org.pojo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 注册中心配置根对象.
 *
 * @author fangyanpeng
 */
@Getter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class RegistryCenterConfigurations {
    private Set<RegistryCenterConfiguration> registryCenterConfiguration = new LinkedHashSet<>();
}
