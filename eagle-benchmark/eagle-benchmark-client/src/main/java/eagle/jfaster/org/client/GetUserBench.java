package eagle.jfaster.org.client;

import eagle.jfaster.org.benchmark.pojo.User;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Create by fangyanpeng 2017/09/08
 */
@Warmup(iterations = 20)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GetUserBench extends BenchBase {

    @Benchmark
    @CompilerControl(CompilerControl.Mode.INLINE)
    public Object cycleQuery() throws Exception {
        User u = service.getUserById(1);
        if (u == null) {
            throw new IllegalStateException();
        }
        return u;
    }

}
