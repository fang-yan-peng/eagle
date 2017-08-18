package eagle.jfaster.org.util;

import com.google.common.base.Strings;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
public class ParserUtil {

    public static void multiRef(String name, String value, BeanDefinitionBuilder beanBuiler) {
        String[] values = value.split("\\s*[,]+\\s*");
        if(values == null || values.length == 0){
            return;
        }
        ManagedList list = new ManagedList();
        for (String v : values) {
            if (!Strings.isNullOrEmpty(v)) {
                list.add(new RuntimeBeanReference(v));
            }
        }
        beanBuiler.addPropertyValue(name, list);
    }
}
