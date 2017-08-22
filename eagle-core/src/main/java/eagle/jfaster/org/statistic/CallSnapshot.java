package eagle.jfaster.org.statistic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
@RequiredArgsConstructor
public class CallSnapshot {

    @Getter
    private final long timestamp;

    @Getter
    private final long times;

    @Getter
    private final long value;

}

