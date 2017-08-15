package eagle.jfaster.org.pool.number;

/**
 * Abstract interface for objects that can concurrently add longs.
 *
 * @author Louis Wasserman
 */
public interface LongAddable {

    void increment();

    void add(long x);

    long sum();

}