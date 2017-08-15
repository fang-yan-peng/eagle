package eagle.jfaster.org.client;

import eagle.jfaster.org.service.Calculate;
import eagle.jfaster.org.service.HelloWorld;
import eagle.jfaster.org.service.Notify;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
public class SyncClient {

    public static void main(String[] args) {
        ApplicationContext appCtx = new ClassPathXmlApplicationContext("client_sync.xml");

        Calculate calculate = appCtx.getBean("calculate1",Calculate.class);
        System.out.println(calculate.add(1,3));
        System.out.println(calculate.sub(8,3));

        HelloWorld helloWorld = appCtx.getBean("hello1",HelloWorld.class);
        System.out.println(helloWorld.hello());
        System.out.println(helloWorld.hellos().size());

        Notify notify = appCtx.getBean("notify1",Notify.class);
        System.out.println(notify.ping("ping"));
        notify.invoke("It is me");

    }
}
