package eagle.jfaster.org.client;

import eagle.jfaster.org.service.Calculate;
import eagle.jfaster.org.service.HelloWorld;
import eagle.jfaster.org.service.Notify;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
public class SyncClient {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("client_sync.xml");
        appCtx.start();
        Calculate calculate = appCtx.getBean("calculate1",Calculate.class);
        int cnt = 0;
        //测试统计
        while (cnt < 30) {
            ++cnt;
            System.out.println(calculate.add(1, 3));
            System.out.println(calculate.sub(8, 3));
            TimeUnit.SECONDS.sleep(2);
        }
        //测试mock
        System.out.println(calculate.div(2,0));
        HelloWorld helloWorld = appCtx.getBean("hello1",HelloWorld.class);
        System.out.println(helloWorld.hello());
        System.out.println(helloWorld.hellos().size());
        //测试无参返回
        Notify notify = appCtx.getBean("notify1",Notify.class);
        System.out.println(notify.ping("ping"));
        notify.invoke("It is me");

    }
}
