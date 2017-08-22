package eagle.jfaster.org.statistic;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.util.DateUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * 统计项集合
 *
 * Created by fangyanpeng on 2017/8/22.
 */
public class StatsItemSet {

    private final ConcurrentMap<String/* key */, StatsItem> statsItemTable = new ConcurrentHashMap<>(4);

    private final String statsName;

    private final ScheduledExecutorService scheduledExecutorService;

    private final InternalLogger log;

    public StatsItemSet(String statsName, ScheduledExecutorService scheduledExecutorService, InternalLogger log) {
        this.statsName = statsName;
        this.scheduledExecutorService = scheduledExecutorService;
        this.log = log;
        this.init();
    }

    public void init() {

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    samplingInSeconds();
                } catch (Throwable ignored) {
                }
            }
        }, 0, 10, TimeUnit.SECONDS);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    samplingInMinutes();
                } catch (Throwable ignored) {
                }
            }
        }, 0, 10, TimeUnit.MINUTES);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    samplingInHour();
                } catch (Throwable ignored) {
                }
            }
        }, 0, 1, TimeUnit.HOURS);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    printAtMinutes();
                } catch (Throwable ignored) {
                }
            }
        }, Math.abs(DateUtil.computNextMinutesTimeMillis() - System.currentTimeMillis()), 1000 * 60, TimeUnit.MILLISECONDS);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    printAtHour();
                } catch (Throwable ignored) {
                }
            }
        }, Math.abs(DateUtil.computNextHourTimeMillis() - System.currentTimeMillis()), 1000 * 60 * 60, TimeUnit.MILLISECONDS);

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    printAtDay();
                } catch (Throwable ignored) {
                }
            }
        }, Math.abs(DateUtil.computNextMorningTimeMillis() - System.currentTimeMillis()), 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);
    }

    private void samplingInSeconds() {
        Iterator<Map.Entry<String, StatsItem>> it = this.statsItemTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, StatsItem> next = it.next();
            next.getValue().samplingInSeconds();
        }
    }

    private void samplingInMinutes() {
        Iterator<Map.Entry<String, StatsItem>> it = this.statsItemTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, StatsItem> next = it.next();
            next.getValue().samplingInMinutes();
        }
    }

    private void samplingInHour() {
        Iterator<Map.Entry<String, StatsItem>> it = this.statsItemTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, StatsItem> next = it.next();
            next.getValue().samplingInHour();
        }
    }

    private void printAtMinutes() {
        Iterator<Map.Entry<String, StatsItem>> it = this.statsItemTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, StatsItem> next = it.next();
            next.getValue().printAtMinutes();
        }
    }

    private void printAtHour() {
        Iterator<Map.Entry<String, StatsItem>> it = this.statsItemTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, StatsItem> next = it.next();
            next.getValue().printAtHour();
        }
    }

    private void printAtDay() {
        Iterator<Map.Entry<String, StatsItem>> it = this.statsItemTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, StatsItem> next = it.next();
            next.getValue().printAtDay();
        }
    }

    public void addValue(final String statsKey, final long incValue, final int incTimes) {
        StatsItem statsItem = this.getAndCreateStatsItem(statsKey);
        statsItem.getValue().addAndGet(incValue);
        statsItem.getTimes().addAndGet(incTimes);
    }

    public StatsItem getAndCreateStatsItem(final String statsKey) {
        StatsItem statsItem = this.statsItemTable.get(statsKey);
        if (null == statsItem) {
            synchronized (this) {
                statsItem = this.statsItemTable.get(statsKey);
                if(statsItem == null){
                    statsItem = new StatsItem(this.statsName, statsKey, this.log);
                    statsItemTable.put(statsKey, statsItem);
                }

            }
        }
        return statsItem;
    }

    public StatsSnapshot getStatsDataInMinute(final String statsKey) {
        StatsItem statsItem = this.statsItemTable.get(statsKey);
        if (null != statsItem) {
            return statsItem.getStatsDataInMinute();
        }
        return new StatsSnapshot();
    }

    public StatsSnapshot getStatsDataInHour(final String statsKey) {
        StatsItem statsItem = this.statsItemTable.get(statsKey);
        if (null != statsItem) {
            return statsItem.getStatsDataInHour();
        }
        return new StatsSnapshot();
    }

    public StatsSnapshot getStatsDataInDay(final String statsKey) {
        StatsItem statsItem = this.statsItemTable.get(statsKey);
        if (null != statsItem) {
            return statsItem.getStatsDataInDay();
        }
        return new StatsSnapshot();
    }

    public StatsItem getStatsItem(final String statsKey) {
        return this.statsItemTable.get(statsKey);
    }
}

