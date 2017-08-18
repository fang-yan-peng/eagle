package eagle.jfaster.org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by fangyanpeng on 2017/8/17.
 */
@SpringBootApplication
public class EagleUi {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EagleUi.class);

    }
}
