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

import eagle.jfaster.org.benchmark.api.EagleBenchmarkService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Create by fangyanpeng 2017/09/08
 */
@State(Scope.Benchmark)
public abstract class BenchBase {

    @Param({"eagle", "dubbo",})
    public String framework;

    public static EagleBenchmarkService service;

    public static ClassPathXmlApplicationContext ctx;

    @Setup(Level.Trial)
    public void setup(BenchmarkParams params) {
        switch (framework) {
            case "eagle":
                setupEagle();
                break;
            case "dubbo":
                setupDubbo();
                break;
        }
    }

    @TearDown(Level.Trial)
    public void teardown() throws Exception {
        switch (framework) {
            case "eagle":
                ctx.close();
                break;
            case "dubbo":
                ctx.close();
                break;
        }
    }

    private void setupEagle() {
        ctx = new ClassPathXmlApplicationContext("classpath*:benchmark-eagle.xml");
        service = ctx.getBean("eagleBenchmark",EagleBenchmarkService.class);
    }

    private void setupDubbo() {
        ctx = new ClassPathXmlApplicationContext("classpath*:benchmark-dubbo.xml");
        service = ctx.getBean("dubboBenchmark",EagleBenchmarkService.class);
    }

}
