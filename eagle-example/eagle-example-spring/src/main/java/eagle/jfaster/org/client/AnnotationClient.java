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

import eagle.jfaster.org.config.annotation.Refer;
import eagle.jfaster.org.service.Hello;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by fangyanpeng on 2017/8/18.
 */
@Service
public class AnnotationClient {

    @Refer(baseRefer = "baseRefer")
    private Hello hello;

    @Refer(baseRefer = "baseRefer", version = "1.1")
    private Hello hello1;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("client_annotation.xml");
        appCtx.start();
        AnnotationClient client = appCtx.getBean(AnnotationClient.class);
        System.out.println(client.hello.hello());
        System.out.println(client.hello1.hello());
    }
}
