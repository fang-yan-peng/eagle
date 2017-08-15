package eagle.jfaster.org.cluster.loadbalance;

import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 轮询访问refer
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "roundrobin")
public class RoundRobinLoadBalance<T> extends AbstractLoadBalance<T> {

    private AtomicInteger idx = new AtomicInteger(0);

    @Override
    public Refer<T> doSelect(Request request) {
        List<Refer<T>> refers = this.refers;
        int index = getNextPositive();
        for (int i = 0; i < refers.size(); i++) {
            Refer<T> ref = refers.get((i + index) % refers.size());
            if (ref.isAlive()) {
                return ref;
            }
        }
        return null;
    }

    @Override
    public List<Refer<T>> doselectHaRefers(Request request) {
        List<Refer<T>> refers = this.refers;
        List<Refer<T>> haRefers = new ArrayList<>();
        int index = getNextPositive();
        for (int i = 0, count = 0; i < refers.size() && count < MAX_REFER_COUNT; i++) {
            Refer<T> referer = refers.get((i + index) % refers.size());
            if (referer.isAlive()) {
                haRefers.add(referer);
                count++;
            }
        }
        return haRefers;
    }
    // get positive int
    private int getNextPositive() {
        return 0x7fffffff & idx.incrementAndGet();
    }
}

