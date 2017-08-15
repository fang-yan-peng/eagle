package eagle.jfaster.org;

import eagle.jfaster.org.service.Calculate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@SpringBootApplication
public class ReferSartup {

    public static void main(String[] args) {
        ApplicationContext ctx =  SpringApplication.run(ServiceStartup.class, args);
        Calculate calculate = (Calculate) ctx.getBean("calculate1");
        System.out.println(calculate.add(1,2));
    }
}
