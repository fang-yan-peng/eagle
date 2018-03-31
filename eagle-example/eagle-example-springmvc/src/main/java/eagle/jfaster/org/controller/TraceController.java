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
package eagle.jfaster.org.controller;

import eagle.jfaster.org.config.annotation.Refer;
import eagle.jfaster.org.service.Calculate;
import eagle.jfaster.org.service.Hello;
import eagle.jfaster.org.trace.annotation.Trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fangyanpeng on 2017/12/16.
 */
@RestController
@RequestMapping("/")
public class TraceController {

    private static final Logger logger = LoggerFactory.getLogger(TraceController.class);

    @Refer(baseRefer = "baseRefer")
    private Calculate calculate;

    @Refer(baseRefer = "baseRefer")
    private Hello hello;

    @Trace
    @RequestMapping("/cal")
    public String cal(@RequestParam int a, @RequestParam int b, @RequestParam int code) {
        logger.info(hello.hello(code));
        int res = calculate.add(a, b);
        logger.info("calculate {}", res);
        return String.valueOf(res);
    }
}
