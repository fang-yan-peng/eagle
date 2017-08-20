package eagle.jfaster.org.cluster.loadbalance;

import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机访问refer
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "random")
public class RandomLoadBalance <T> extends AbstractLoadBalance <T> {

    @Override
    public Refer<T> doSelect(Request request) {
        List<Refer<T>> refers = this.refers;
        int idx = ThreadLocalRandom.current().nextInt(refers.size());
        for (int i = 0; i < refers.size(); i++) {
            Refer<T> ref = refers.get((i + idx) % refers.size());
            if (ref.isAlive()) {
                return ref;
            }
        }
        return null;
    }
}
