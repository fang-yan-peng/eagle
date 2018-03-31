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

package eagle.jfaster.org.client.pool;

import eagle.jfaster.org.client.channel.AbstractNettyChannel;
import eagle.jfaster.org.client.NettyClient;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.pool.ConcurrentBag;
import eagle.jfaster.org.util.ClockSource;
import eagle.jfaster.org.pool.ConcurrentBag.IBagStateListener;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static eagle.jfaster.org.client.pool.NettyPoolEntry.LASTACCESS_COMPARABLE;
import static eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry.STATE_IN_USE;
import static eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry.STATE_NOT_IN_USE;
import static eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry.STATE_REMOVED;
import static eagle.jfaster.org.util.UtilityUtil.createThreadPoolExecutor;
import static eagle.jfaster.org.util.UtilityUtil.quietlySleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 连接池，每个client对应一个连接池
 *
 * Created by fangyanpeng1 on 2017/8/2.
 */
public class NettySharedConnPool implements IBagStateListener {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettySharedConnPool.class);


    private static final ClockSource clockSource = ClockSource.INSTANCE;

    private static final int POOL_NORMAL = 0;

    private static final int POOL_SUSPENDED = 1;

    private static final int POOL_SHUTDOWN = 2;

    private volatile int poolState;

    private final long ALIVE_BYPASS_WINDOW_MS = Long.getLong("eagle.jfaster.org.aliveBypassWindowMs", MILLISECONDS.toMillis(500));

    private final long HOUSEKEEPING_PERIOD_MS = Long.getLong("eagle.jfaster.org.housekeeping.periodMs", SECONDS.toMillis(30));

    private final PoolEntryCreator POOL_ENTRY_CREATOR = new PoolEntryCreator();

    private final AtomicInteger totalConnections;

    private final ThreadPoolExecutor addConnectionExecutor;

    private final ThreadPoolExecutor closeConnectionExecutor;

    private final ScheduledThreadPoolExecutor houseKeepingExecutorService;

    private final ConcurrentBag<NettyPoolEntry> connectionBag;

    private final MergeConfig config;

    @Getter
    private final String poolName;

    private final NettyClient client;

    private AtomicInteger poolNum = new AtomicInteger(0);

    public NettySharedConnPool(MergeConfig config, NettyClient client) {
        poolName = "eagleClientPool-" + poolNum.getAndIncrement();
        this.config = config;
        this.client = client;
        totalConnections = new AtomicInteger(0);
        connectionBag = new ConcurrentBag<>(this);
        ThreadFactory threadFactory = new DefaultThreadFactory(poolName + " housekeeper", true);
        int maxClientConnection = config.getExtInt(ConfigEnum.maxClientConnection.getName(), ConfigEnum.maxClientConnection.getIntValue());
        this.addConnectionExecutor = createThreadPoolExecutor(maxClientConnection, poolName + " connection adder", threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        this.closeConnectionExecutor = createThreadPoolExecutor(maxClientConnection, poolName + " connection closer", threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        this.houseKeepingExecutorService = new ScheduledThreadPoolExecutor(1, threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        this.houseKeepingExecutorService.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.houseKeepingExecutorService.setRemoveOnCancelPolicy(true);
        this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L, HOUSEKEEPING_PERIOD_MS, MILLISECONDS);
        fillPool();
    }

    public final AbstractNettyChannel getConnection() {
        long connectionTimeout = config.getExtLong(ConfigEnum.connectTimeout.getName(), ConfigEnum.connectTimeout.getLongValue());
        final long startTime = clockSource.currentTime();

        try {
            long timeout = connectionTimeout;
            do {
                final NettyPoolEntry poolEntry = connectionBag.borrow(timeout, MILLISECONDS);
                if (poolEntry == null) {
                    break;
                }
                final long now = clockSource.currentTime();
                if (poolEntry.isMarkedEvicted() || (clockSource.elapsedMillis(poolEntry.lastAccessed, now) > ALIVE_BYPASS_WINDOW_MS && !connectionAlive(poolEntry.connection))) {
                    closeConnection(poolEntry);
                    timeout = connectionTimeout - clockSource.elapsedMillis(startTime);
                } else {
                    return poolEntry.getConnection();
                }
            } while (timeout > 0L);
        } catch (InterruptedException e) {
            throw new EagleFrameException(poolName + " - Interrupted during connection acquisition", e);
        }
        throw new EagleFrameException("get connection timeout,timeout:%d", connectionTimeout);
    }

    public final void release(AbstractNettyChannel connection) {
        NettyPoolEntry poolEntry = connection.getPoolEntry();
        poolEntry.lastAccessed = clockSource.currentTime();
        connectionBag.requite(poolEntry);

    }

    private void fillPool() {
        int maxClientConnection = config.getExtInt(ConfigEnum.maxClientConnection.getName(), ConfigEnum.maxClientConnection.getIntValue());
        int minClientConnection = config.getExtInt(ConfigEnum.minClientConnection.getName(), ConfigEnum.minClientConnection.getIntValue());
        final int connectionsToAdd = Math.min(maxClientConnection - totalConnections.get(), minClientConnection - getIdleConnections())
                - addConnectionExecutor.getQueue().size();
        for (int i = 0; i < connectionsToAdd; i++) {
            addBagItem();
        }
    }

    private boolean connectionAlive(AbstractNettyChannel channel) {
        return channel.getChannel() != null && channel.getChannel().isActive();
    }

    private final void closeConnection(final NettyPoolEntry poolEntry) {
        if (connectionBag.remove(poolEntry)) {
            final int tc = totalConnections.decrementAndGet();
            if (tc < 0) {
                logger.warn("{} - Unexpected value of totalConnections={}", poolName, tc);
            }
            final AbstractNettyChannel connection = poolEntry.close();
            //解除NettyChannel 和 NettyPoolEntry的依赖
            connection.setPoolEntry(null);
            closeConnectionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    connection.close();
                }
            });
        }
    }

    private void softEvictConnection(final NettyPoolEntry poolEntry, final boolean owner) {
        if (owner || connectionBag.reserve(poolEntry)) {
            poolEntry.markEvicted();
            closeConnection(poolEntry);
        } else {
            poolEntry.markEvicted();
        }
    }

    public void invalidateConnection(AbstractNettyChannel channel) {
        softEvictConnection(channel.getPoolEntry(), false);
    }

    public void invalidateConnection(Channel channel) {
        for (NettyPoolEntry poolEntry : connectionBag.values()) {
            if (channel.equals(poolEntry.getConnection().getChannel())) {
                softEvictConnection(poolEntry, false);
                return;
            }
        }
        logger.warn("invalidateConnection not find channel to evict");
    }

    public void softEvictConnections() {
        for (NettyPoolEntry poolEntry : connectionBag.values()) {
            softEvictConnection(poolEntry, false);
        }
    }

    private NettyPoolEntry createPoolEntry() {
        try {
            final NettyPoolEntry poolEntry = newPoolEntry();
            final long maxLifetime = config.getExtLong(ConfigEnum.maxLifetime.getName(), ConfigEnum.maxLifetime.getLongValue());
            if (maxLifetime > 0) {
                final long variance = maxLifetime > 10_000 ? ThreadLocalRandom.current().nextLong(maxLifetime / 40) : 0;
                final long lifetime = maxLifetime - variance;
                poolEntry.setFutureEol(houseKeepingExecutorService.schedule(new Runnable() {
                    @Override
                    public void run() {
                        softEvictConnection(poolEntry, false);
                    }
                }, lifetime, MILLISECONDS));
            }
            return poolEntry;
        } catch (Exception e) {
            if (poolState == POOL_NORMAL) {
                logger.info("{} - Cannot acquire connection", poolName, e);
            }
            return null;
        }
    }

    private NettyPoolEntry newPoolEntry() throws Exception {
        AbstractNettyChannel channel = newConnection();
        NettyPoolEntry poolEntry = new NettyPoolEntry(channel, this);
        channel.setPoolEntry(poolEntry);
        return poolEntry;
    }

    private AbstractNettyChannel newConnection() throws Exception {
        AbstractNettyChannel connection = null;
        try {
            connection = client.newChannel();
            return connection;
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    public final int getActiveConnections() {
        return connectionBag.getCount(STATE_IN_USE);
    }

    public final int getIdleConnections() {
        return connectionBag.getCount(STATE_NOT_IN_USE);
    }

    public final int getTotalConnections() {
        return connectionBag.size() - connectionBag.getCount(STATE_REMOVED);
    }

    public final int getThreadsAwaitingConnection() {
        return connectionBag.getPendingQueue();
    }


    @Override
    public Future<Boolean> addBagItem() {
        return addConnectionExecutor.submit(POOL_ENTRY_CREATOR);
    }


    public final synchronized void shutdown() throws InterruptedException {
        try {
            poolState = POOL_SHUTDOWN;
            logger.info("{} - Close initiated...", poolName);
            softEvictConnections();
            if (addConnectionExecutor != null) {
                addConnectionExecutor.shutdown();
                addConnectionExecutor.awaitTermination(5L, SECONDS);
            }
            if (houseKeepingExecutorService != null) {
                houseKeepingExecutorService.shutdown();
                houseKeepingExecutorService.awaitTermination(5L, SECONDS);
            }
            connectionBag.close();

            final long start = clockSource.currentTime();
            //等待5秒钟，等待应用归还连接。
            do {
                softEvictConnections();
            }
            while (getTotalConnections() > 0 && clockSource.elapsedMillis(start) < SECONDS.toMillis(5));

            for (NettyPoolEntry poolEntry : connectionBag.values()) {
                closeConnection(poolEntry);
            }

            if (closeConnectionExecutor != null) {
                closeConnectionExecutor.shutdown();
                closeConnectionExecutor.awaitTermination(5L, SECONDS);
            }
        } finally {
            logger.info("{} - Closed.", poolName);
        }
    }


    private class HouseKeeper implements Runnable {
        private volatile long previous = clockSource.plusMillis(clockSource.currentTime(), -HOUSEKEEPING_PERIOD_MS);

        @Override
        public void run() {
            try {
                long idleTimeout = config.getExtLong(ConfigEnum.idleTime.getName(), ConfigEnum.idleTime.getLongValue());
                final long now = clockSource.currentTime();
                if (clockSource.plusMillis(now, 128) < clockSource.plusMillis(previous, HOUSEKEEPING_PERIOD_MS)) {
                    logger.warn("{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.",
                            clockSource.elapsedDisplayString(previous, now), poolName);
                    previous = now;
                    softEvictConnections();
                    fillPool();
                    return;
                } else if (now > clockSource.plusMillis(previous, (3 * HOUSEKEEPING_PERIOD_MS) / 2)) {
                    logger.warn("{} - Thread starvation or clock leap detected (housekeeper delta={}).", clockSource.elapsedDisplayString(previous, now), poolName);
                }
                previous = now;
                if (idleTimeout > 0L) {
                    final List<NettyPoolEntry> idleList = connectionBag.values(STATE_NOT_IN_USE);
                    int minClientConnection = config.getExtInt(ConfigEnum.minClientConnection.getName(), ConfigEnum.minClientConnection.getIntValue());
                    int removable = idleList.size() - minClientConnection;
                    if (removable > 0) {
                        Collections.sort(idleList, LASTACCESS_COMPARABLE);
                        for (NettyPoolEntry poolEntry : idleList) {
                            if (clockSource.elapsedMillis(poolEntry.lastAccessed, now) > idleTimeout && connectionBag.reserve(poolEntry)) {
                                closeConnection(poolEntry);
                                if (--removable == 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
                fillPool();
            } catch (Exception e) {
                logger.error("Unexpected exception in housekeeping task", e);
            }
        }
    }


    private class PoolEntryCreator implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            long sleepBackoff = 250L;
            int maxClientConnection = config.getExtInt(ConfigEnum.maxClientConnection.getName(), ConfigEnum.maxClientConnection.getIntValue());
            long connectionTimeout = config.getExtLong(ConfigEnum.connectTimeout.getName(), ConfigEnum.connectTimeout.getLongValue());
            while (poolState == POOL_NORMAL && totalConnections.get() < maxClientConnection) {
                final NettyPoolEntry poolEntry = createPoolEntry();
                if (poolEntry != null) {
                    totalConnections.incrementAndGet();
                    connectionBag.add(poolEntry);
                    return Boolean.TRUE;
                }

                quietlySleep(sleepBackoff);
                sleepBackoff = Math.min(SECONDS.toMillis(10), Math.min(connectionTimeout, (long) (sleepBackoff * 1.5)));
            }
            return Boolean.FALSE;
        }
    }


}
