
package eagle.jfaster.org.repository;

/**
 * 基于XML的数据访问器.
 * 
 * @param <E> 数据类型
 * 
 * @author fangyanpeng
 */
public interface XmlRepository<E> {
    
    /**
     * 读取数据.
     * 
     * @return 数据
     */
    E load();
    
    /**
     * 存储数据.
     * 
     * @param entity 数据
     */
    void save(E entity);
}
