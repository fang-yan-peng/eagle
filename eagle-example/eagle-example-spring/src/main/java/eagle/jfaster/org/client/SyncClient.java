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
