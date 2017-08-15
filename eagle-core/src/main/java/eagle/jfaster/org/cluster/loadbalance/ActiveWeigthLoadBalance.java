package eagle.jfaster.org.cluster.loadbalance;

import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 低并发refer优先访问
 *
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "activeWeigth")
public class ActiveWeigthLoadBalance<T> extends AbstractLoadBalance<T> {

    @Override
    public Refer<T> doSelect(Request request) {
        List<Refer<T>> refers = this.refers;
        int refererSize = refers.size();
        int startIndex = ThreadLocalRandom.current().nextInt(refererSize);
        int currentCursor = 0;
        int currentAvailableCursor = 0;
        Refer<T> referer = null;
        while (currentAvailableCursor < MAX_REFER_COUNT && currentCursor < refererSize) {
            Refer<T> temp = refers.get((startIndex + currentCursor) % refererSize);
            currentCursor++;

            if (!temp.isAlive()) {
                continue;
            }
            currentAvailableCursor++;
            if (referer == null) {
                referer = temp;
            } else {
                if (compare(referer, temp) > 0) {
                    referer = temp;
                }
            }
        }

        return referer;
    }

    @Override
    public List<Refer<T>> doselectHaRefers(Request request) {
        List<Refer<T>> refers = this.refers;
        List<Refer<T>> haRefers = new ArrayList<>();
        int refererSize = refers.size();
        int startIndex = ThreadLocalRandom.current().nextInt(refererSize);
        int currentCursor = 0;
        int currentAvailableCursor = 0;

        while (currentAvailableCursor < MAX_REFER_COUNT && currentCursor < refererSize) {
            Refer<T> temp = refers.get((startIndex + currentCursor) % refererSize);
            currentCursor++;
            if (!temp.isAlive()) {
                continue;
            }
            currentAvailableCursor++;
            haRefers.add(temp);
        }
        Collections.sort(haRefers, new LowActivePriorityComparator<T>());
        return haRefers;
    }

    private int compare(Refer<T> referer1, Refer<T> referer2) {
        return referer1.getActiveCount() - referer2.getActiveCount();
    }

    static class LowActivePriorityComparator<T> implements Comparator<Refer<T>> {
        @Override
        public int compare(Refer<T> referer1, Refer<T> referer2) {
            return referer1.getActiveCount() - referer2.getActiveCount();
        }
    }


}
