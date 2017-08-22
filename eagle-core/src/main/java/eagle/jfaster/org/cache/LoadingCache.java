package eagle.jfaster.org.cache;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
public interface LoadingCache<K, V> {

    public V get(K key);

}
