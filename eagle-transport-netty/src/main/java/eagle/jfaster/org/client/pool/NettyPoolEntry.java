package eagle.jfaster.org.client.pool;

import eagle.jfaster.org.client.channel.AbstractNettyChannel;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.util.ClockSource;
import java.util.Comparator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry;
import lombok.Getter;

/**
 * 连接池缓存对象
 *
 * Created by fangyanpeng1 on 2017/8/2.
 */
public final class NettyPoolEntry implements IConcurrentBagEntry {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyPoolEntry.class);


    static final Comparator<NettyPoolEntry> LASTACCESS_COMPARABLE;
    @Getter
    AbstractNettyChannel connection;
    long lastAccessed;
    long lastBorrowed;
    private volatile boolean evict;
    private volatile ScheduledFuture<?> endOfLife;
    private final NettySharedConnPool sharedConnPool;
    private final AtomicInteger state;

    static {
        LASTACCESS_COMPARABLE = new Comparator<NettyPoolEntry>() {
            @Override
            public int compare(final NettyPoolEntry entryOne, final NettyPoolEntry entryTwo) {
                return Long.compare(entryOne.lastAccessed, entryTwo.lastAccessed);
            }
        };
    }

    public NettyPoolEntry(final AbstractNettyChannel connection, final NettySharedConnPool sharedConnPool) {
        this.connection = connection;
        this.sharedConnPool = sharedConnPool;
        this.state = new AtomicInteger();
        this.lastAccessed = ClockSource.INSTANCE.currentTime();
    }


    public void setFutureEol(final ScheduledFuture<?> endOfLife)
    {
        this.endOfLife = endOfLife;
    }


    public String getPoolName() {
        return sharedConnPool.getPoolName();
    }

    public boolean isMarkedEvicted() {
        return evict;
    }

    public void markEvicted() {
        this.evict = true;
    }

    public long getMillisSinceBorrowed() {
        return ClockSource.INSTANCE.elapsedMillis(lastBorrowed);
    }

    @Override
    public String toString() {
        final long now = ClockSource.INSTANCE.currentTime();
        return connection
                + ", accessed " + ClockSource.INSTANCE.elapsedDisplayString(lastAccessed, now) + " ago, "
                + stateToString();
    }

    // ***********************************************************************
    //                      IConcurrentBagEntry methods
    // ***********************************************************************

    @Override
    public int getState() {
        return state.get();
    }

    @Override
    public boolean compareAndSet(int expect, int update) {
        return state.compareAndSet(expect, update);
    }

    @Override
    public void lazySet(int update) {
        state.lazySet(update);
    }

    public AbstractNettyChannel close() {
        ScheduledFuture<?> eol = endOfLife;
        if (eol != null && !eol.isDone() && !eol.cancel(false)) {
            logger.warn("{} - maxLifeTime expiration task cancellation unexpectedly returned false for connection {}", getPoolName(), connection);
        }

        AbstractNettyChannel con = connection;
        connection = null;
        endOfLife = null;
        return con;
    }

    private String stateToString() {
        switch (state.get()) {
        case STATE_IN_USE:
            return "IN_USE";
        case STATE_NOT_IN_USE:
            return "NOT_IN_USE";
        case STATE_REMOVED:
            return "REMOVED";
        case STATE_RESERVED:
            return "RESERVED";
        default:
            return "Invalid";
        }
    }
}

