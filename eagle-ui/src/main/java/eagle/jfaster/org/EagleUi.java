package eagle.jfaster.org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author fangyanpeng
 */
@SpringBootApplication
public class EagleUi {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EagleUi.class,args);

    }
}
