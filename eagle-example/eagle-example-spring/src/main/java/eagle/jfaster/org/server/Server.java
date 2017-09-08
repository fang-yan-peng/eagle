package eagle.jfaster.org.server;

import eagle.jfaster.org.EmbedZookeeperServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * Created by fangyanpeng1 on 2017/8/9.
 */
public class Server {

    public static final int EMBED_ZOOKEEPER_PORT = 4181;

    public static void main(String[] args) throws InterruptedException {
        EmbedZookeeperServer.start(EMBED_ZOOKEEPER_PORT);
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("server.xml");
        appCtx.start();
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
