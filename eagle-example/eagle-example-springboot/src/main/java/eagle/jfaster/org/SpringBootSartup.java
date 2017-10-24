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

package eagle.jfaster.org;

import eagle.jfaster.org.service.Calculator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by fangyanpeng1 on 2017/8/11.
 */
@SpringBootApplication
public class SpringBootSartup {

    public static void main(String[] args) throws InterruptedException {
        //启动Curator框架提供的内置zookeeper 仅供测试使用，生产环境请使用真实zookeeper地址
        EmbedZookeeperServer.start(4181);
        ApplicationContext ctx =  SpringApplication.run(SpringBootSartup.class, args);
        TimeUnit.SECONDS.sleep(2);
        Calculator calculator = ctx.getBean(Calculator.class);
        System.out.println(calculator.calculate.add(1,2));
        System.out.println(calculator.calculate.sub(9,5));
    }
}
