package eagle.jfaster.org.client;

import eagle.jfaster.org.service.Hello;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
public class AnnotationClient {
    public static void main(String[] args) {
        ApplicationContext appCtx = new ClassPathXmlApplicationContext("client_annotation.xml");
        Hello hello = appCtx.getBean("helloAnno",Hello.class);
        System.out.println(hello.hello());
    }
}
