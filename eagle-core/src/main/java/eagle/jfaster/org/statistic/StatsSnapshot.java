package eagle.jfaster.org.statistic;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
public class StatsSnapshot {

    @Getter
    @Setter
    private long sum;

    @Getter
    @Setter
    private double tps;

    @Getter
    @Setter
    private double avgpt;
}
