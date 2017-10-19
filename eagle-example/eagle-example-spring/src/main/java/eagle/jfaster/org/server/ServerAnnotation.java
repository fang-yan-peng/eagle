/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

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
        //启动Curator框架提供的内置zookeeper 仅供测试使用，生产环境请使用真实zookeeper地址
        EmbedZookeeperServer.start(EMBED_ZOOKEEPER_PORT);
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("server_annotation.xml");
        appCtx.start();
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
