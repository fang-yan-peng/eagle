package eagle.jfaster.org.server;

import eagle.jfaster.org.EmbedZookeeperServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

import static eagle.jfaster.org.server.Server.EMBED_ZOOKEEPER_PORT;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
public class ServerAnnotation {
    public static void main(String[] args) throws InterruptedException {
        EmbedZookeeperServer.start(EMBED_ZOOKEEPER_PORT);
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("server_annotation.xml");
        appCtx.start();
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
