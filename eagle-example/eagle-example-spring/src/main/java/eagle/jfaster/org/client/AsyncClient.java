package eagle.jfaster.org.client;

import eagle.jfaster.org.service.Calculate;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
public class AsyncClient {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("client_async.xml");
        appCtx.start();
        Calculate calculate = appCtx.getBean("calculate2",Calculate.class);
        calculate.add(1,3);
        calculate.sub(34,9);
        while (true) {
            TimeUnit.SECONDS.sleep(5);
        }
    }
}
