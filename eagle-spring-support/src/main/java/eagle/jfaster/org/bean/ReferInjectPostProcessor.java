package eagle.jfaster.org.bean;

import com.google.common.base.Strings;
import eagle.jfaster.org.config.annotation.Refer;
import eagle.jfaster.org.context.ReferContext;
import eagle.jfaster.org.exception.EagleFrameException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;

/**
 * Created by fangyanpeng on 2017/10/24.
 */
public class ReferInjectPostProcessor implements BeanPostProcessor,ApplicationContextAware {

    private ConfigurableApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            Class<?> beanClass = bean.getClass();
            Field[] fields;
            try {
                fields = beanClass.getDeclaredFields();
            } catch (Throwable e) {
                return bean;
            }
            if(fields != null && fields.length != 0){
                for (Field field : fields){
                    if(field.isAnnotationPresent(Refer.class)){
                        Refer refer = field.getAnnotation(Refer.class);
                        field.setAccessible(true);
                        String id = ReferContext.getName(refer);
                        if(Strings.isNullOrEmpty(id)){
                            continue;
                        }
                        field.set(bean,ctx.getBean(id));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new EagleFrameException(e);
        }
        return bean;
    }
}
