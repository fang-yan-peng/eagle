package eagle.jfaster.org.cache;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
public interface CacheLoader<K, V> {

    public V load(K key);

}
