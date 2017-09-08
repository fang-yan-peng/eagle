package eagle.jfaster.org.client;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
* Create by fangyanpeng 2017/09/08
*/
@Warmup(iterations = 20)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class EchoBench extends BenchBase {

    @Benchmark
    @CompilerControl(CompilerControl.Mode.INLINE)
    public Object cycleQuery() throws Exception {
        String echo = service.echo();
        if (echo == null) {
            throw new IllegalStateException();
        }
        return echo;
    }

}
