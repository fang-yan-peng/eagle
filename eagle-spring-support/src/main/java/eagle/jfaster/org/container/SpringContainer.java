package eagle.jfaster.org.container;

import com.google.common.base.Strings;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.spi.SpiInfo;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by fangyanpeng1 on 2017/8/14.
 */
@SpiInfo(name = "spring")
public class SpringContainer implements Container {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(SpringContainer.class);

    public static final String SPRING_CONFIG = "eagle.spring.config";

    public static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";

    static ClassPathXmlApplicationContext context;

    public static ClassPathXmlApplicationContext getContext() {
        return context;
    }

    @Override
    public void start() {
        String configPath = System.getProperty(SPRING_CONFIG);
        if (Strings.isNullOrEmpty(configPath)) {
            configPath = DEFAULT_SPRING_CONFIG;
        }
        context = new ClassPathXmlApplicationContext(configPath.split("[,\\s]+"));
        context.start();
    }

    @Override
    public void stop() {
        try {
            if (context != null) {
                context.stop();
                context.close();
                context = null;
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }
}
