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

package eagle.jfaster.org.statistic;

import eagle.jfaster.org.logging.InternalLogger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * 统计项，每个接口的一个方法对应一个统计项。
 *
 * Created by fangyanpeng on 2017/8/22.
 */
@RequiredArgsConstructor
public class StatsItem {

    @Getter
    private final AtomicLong value = new AtomicLong(0);

    @Getter
    private final AtomicLong times = new AtomicLong(0);

    private final LinkedList<CallSnapshot> csListMinute = new LinkedList<>();

    private final LinkedList<CallSnapshot> csListHour = new LinkedList<>();

    private final LinkedList<CallSnapshot> csListDay = new LinkedList<>();

    @Getter
    private final String statsName;

    @Getter
    private final String statsKey;

    private final InternalLogger log;

    private StatsSnapshot computeStatsData(final LinkedList<CallSnapshot> csList) {
        StatsSnapshot statsSnapshot = new StatsSnapshot();
        synchronized (csList) {
            double tps = 0;
            double avgpt = 0;
            long sum = 0;
            if (!csList.isEmpty()) {
                CallSnapshot first = csList.getFirst();
                CallSnapshot last = csList.getLast();
                sum = last.getValue() - first.getValue();
                long timesDiff = last.getTimes() - first.getTimes();
                tps = (timesDiff * 1000.0d) / (last.getTimestamp() - first.getTimestamp());
                if (timesDiff > 0) {
                    avgpt = (sum * 1.0d) / timesDiff;
                }
            }

            statsSnapshot.setSum(sum);
            statsSnapshot.setTps(tps);
            statsSnapshot.setAvgpt(avgpt);
        }

        return statsSnapshot;
    }

    public StatsSnapshot getStatsDataInMinute() {
        return computeStatsData(this.csListMinute);
    }

    public StatsSnapshot getStatsDataInHour() {
        return computeStatsData(this.csListHour);
    }

    public StatsSnapshot getStatsDataInDay() {
        return computeStatsData(this.csListDay);
    }

    public void samplingInSeconds() {
        synchronized (this.csListMinute) {
            this.csListMinute.add(new CallSnapshot(System.currentTimeMillis(), this.times.get(), this.value.get()));
            if (this.csListMinute.size() > 7) {
                this.csListMinute.removeFirst();
            }
        }
    }

    public void samplingInMinutes() {
        synchronized (this.csListHour) {
            this.csListHour.add(new CallSnapshot(System.currentTimeMillis(), this.times.get(), this.value.get()));
            if (this.csListHour.size() > 7) {
                this.csListHour.removeFirst();
            }
        }
    }

    public void samplingInHour() {
        synchronized (this.csListDay) {
            this.csListDay.add(new CallSnapshot(System.currentTimeMillis(), this.times.get(), this.value.get()));
            if (this.csListDay.size() > 25) {
                this.csListDay.removeFirst();
            }
        }
    }

    public void printAtMinutes() {
        StatsSnapshot ss = computeStatsData(this.csListMinute);
        log.info(String.format("[%s] [%s] Stats In One Minute, SUM: %d ms TPS: %.2f AVGPT: %.2f ms",
                this.statsName,
                this.statsKey,
                ss.getSum(),
                ss.getTps(),
                ss.getAvgpt()));
    }

    public void printAtHour() {
        StatsSnapshot ss = computeStatsData(this.csListHour);
        log.info(String.format("[%s] [%s] Stats In One Hour, SUM: %d ms TPS: %.2f AVGPT: %.2f ms",
                this.statsName,
                this.statsKey,
                ss.getSum(),
                ss.getTps(),
                ss.getAvgpt()));
    }

    public void printAtDay() {
        StatsSnapshot ss = computeStatsData(this.csListDay);
        log.info(String.format("[%s] [%s] Stats In One Day, SUM: %dms TPS: %.2f AVGPT: %.2f ms",
                this.statsName,
                this.statsKey,
                ss.getSum(),
                ss.getTps(),
                ss.getAvgpt()));
    }
}
