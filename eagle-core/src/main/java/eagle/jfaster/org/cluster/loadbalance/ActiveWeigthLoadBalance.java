package eagle.jfaster.org.cluster.loadbalance;

import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;


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
        int referSize = refers.size();
        int startIndex = ThreadLocalRandom.current().nextInt(referSize);
        int currentCursor = 0;
        int currentAvailableCursor = 0;
        Refer<T> refer = null;
        while (currentAvailableCursor < MAX_REFER_COUNT && currentCursor < referSize) {
            Refer<T> temp = refers.get((startIndex + currentCursor) % referSize);
            currentCursor++;

            if (!temp.isAlive()) {
                continue;
            }
            currentAvailableCursor++;
            if (refer == null) {
                refer = temp;
            } else {
                if (compare(refer, temp) > 0) {
                    refer = temp;
                }
            }
        }

        return refer;
    }

    private int compare(Refer<T> refer1, Refer<T> refer2) {
        return refer1.getActiveCount() - refer2.getActiveCount();
    }

}
