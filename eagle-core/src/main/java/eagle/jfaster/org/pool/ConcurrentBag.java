package eagle.jfaster.org.pool;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry;

import static eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry.STATE_IN_USE;
import static eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry.STATE_NOT_IN_USE;
import static eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry.STATE_REMOVED;
import static eagle.jfaster.org.pool.ConcurrentBag.IConcurrentBagEntry.STATE_RESERVED;


/**
 * 连接存储
 *
 * Created by fangyanpeng1 on 2017/8/2.
 */
public class ConcurrentBag<T extends IConcurrentBagEntry> implements AutoCloseable {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ConcurrentBag.class);


    private final QueuedSequenceSynchronizer synchronizer;
    private final CopyOnWriteArrayList<T> sharedList;
    private final boolean weakThreadLocals;
    private final ThreadLocal<List<Object>> threadList;
    private final IBagStateListener listener;
    private final AtomicInteger waiters;
    private volatile boolean closed;

    public interface IConcurrentBagEntry {
        int STATE_NOT_IN_USE = 0;
        int STATE_IN_USE = 1;
        int STATE_REMOVED = -1;
        int STATE_RESERVED = -2;

        boolean compareAndSet(int expectState, int newState);
        void lazySet(int newState);
        int getState();
    }

    public interface IBagStateListener {
        Future<Boolean> addBagItem();
    }

    public ConcurrentBag(final IBagStateListener listener) {
        this.listener = listener;
        this.weakThreadLocals = useWeakThreadLocals();

        this.waiters = new AtomicInteger();
        this.sharedList = new CopyOnWriteArrayList<>();
        this.synchronizer = new QueuedSequenceSynchronizer();
        if (weakThreadLocals) {
            this.threadList = new ThreadLocal<>();
        }
        else {
            this.threadList = new ThreadLocal<List<Object>>() {
                @Override
                protected List<Object> initialValue()
                {
                    return new FastList<>(IConcurrentBagEntry.class, 16);
                }
            };
        }
    }


    public T borrow(long timeout, final TimeUnit timeUnit) throws InterruptedException {
        // Try the thread-local list first
        List<Object> list = threadList.get();
        if (weakThreadLocals && list == null) {
            list = new ArrayList<>(16);
            threadList.set(list);
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            final Object entry = list.remove(i);
            final T bagEntry = weakThreadLocals ? ((WeakReference<T>) entry).get() : (T) entry;
            if (bagEntry != null && bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
                return bagEntry;
            }
        }

        timeout = timeUnit.toNanos(timeout);
        Future<Boolean> addItemFuture = null;
        final long startScan = System.nanoTime();
        final long originTimeout = timeout;
        long startSeq;
        waiters.incrementAndGet();
        try {
            do {
                do {
                    startSeq = synchronizer.currentSequence();
                    for (T bagEntry : sharedList) {
                        if (bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
                            if (waiters.get() > 1 && addItemFuture == null) {
                                listener.addBagItem();
                            }

                            return bagEntry;
                        }
                    }
                } while (startSeq < synchronizer.currentSequence());

                if (addItemFuture == null || addItemFuture.isDone()) {
                    addItemFuture = listener.addBagItem();
                }

                timeout = originTimeout - (System.nanoTime() - startScan);
            } while (timeout > 10_000L && synchronizer.waitUntilSequenceExceeded(startSeq, timeout));
        }
        finally {
            waiters.decrementAndGet();
        }

        return null;
    }

    public void requite(final T bagEntry) {
        bagEntry.lazySet(STATE_NOT_IN_USE);

        final List<Object> threadLocalList = threadList.get();
        if (threadLocalList != null) {
            threadLocalList.add(weakThreadLocals ? new WeakReference<>(bagEntry) : bagEntry);
        }

        synchronizer.signal();
    }


    public void add(final T bagEntry) {
        if (closed) {
            logger.info("ConcurrentBag has been closed, ignoring add()");
            throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
        }

        sharedList.add(bagEntry);
        synchronizer.signal();
    }


    public boolean remove(final T bagEntry) {
        if (!bagEntry.compareAndSet(STATE_IN_USE, STATE_REMOVED) && !bagEntry.compareAndSet(STATE_RESERVED, STATE_REMOVED) && !closed) {
            logger.warn("Attempt to remove an object from the bag that was not borrowed or reserved: {}", bagEntry);
            return false;
        }

        final boolean removed = sharedList.remove(bagEntry);
        if (!removed && !closed) {
            logger.warn("Attempt to remove an object from the bag that does not exist: {}", bagEntry);
        }

        // synchronizer.signal();
        return removed;
    }


    @Override
    public void close() {
        closed = true;
    }


    public List<T> values(final int state) {
        final ArrayList<T> list = new ArrayList<>(sharedList.size());
        for (final T entry : sharedList) {
            if (entry.getState() == state) {
                list.add(entry);
            }
        }

        return list;
    }

    public List<T> values() {
        return (List<T>) sharedList.clone();
    }


    public boolean reserve(final T bagEntry) {
        return bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_RESERVED);
    }


    public void unreserve(final T bagEntry) {
        if (bagEntry.compareAndSet(STATE_RESERVED, STATE_NOT_IN_USE)) {
            synchronizer.signal();
        }
        else {
            logger.warn("Attempt to relinquish an object to the bag that was not reserved: {}", bagEntry);
        }
    }


    public int getPendingQueue() {
        return synchronizer.getQueueLength();
    }


    public int getCount(final int state) {
        int count = 0;
        for (final T entry : sharedList) {
            if (entry.getState() == state) {
                count++;
            }
        }
        return count;
    }

    public int size() {
        return sharedList.size();
    }

    public void dumpState() {
        for (T bagEntry : sharedList) {
            logger.info(bagEntry.toString());
        }
    }


    private boolean useWeakThreadLocals() {
        try {
            if (System.getProperty("eagle.jfaster.org.pool.useWeakReferences") != null) {
                return Boolean.getBoolean("eagle.jfaster.org.pool.useWeakReferences");
            }

            return getClass().getClassLoader() != ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException se) {
            return true;
        }
    }
}

