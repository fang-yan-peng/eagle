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
