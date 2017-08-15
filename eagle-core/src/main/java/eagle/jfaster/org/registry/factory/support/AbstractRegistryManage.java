package eagle.jfaster.org.registry.factory.support;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.registry.RegistryCenter;
import eagle.jfaster.org.registry.factory.RegistryCenterManage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
public abstract class AbstractRegistryManage implements RegistryCenterManage {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(AbstractRegistryManage.class);


    private static ConcurrentHashMap<String, RegistryCenter> registries = new ConcurrentHashMap<>();

    private ReadWriteLock rdwrLock = new ReentrantReadWriteLock();

    @Override
    public RegistryCenter getRegistry(MergeConfig registryConfig) {
        String registryKey = registryConfig.identity();
        RegistryCenter center = null;
        try {
            rdwrLock.readLock().lock();
            center = registries.get(registryKey);
            if(center == null){
                try {
                    rdwrLock.readLock().unlock();
                    rdwrLock.writeLock().lock();
                    center = createRegistry(registryConfig);
                    registries.put(registryKey,center);
                } finally {
                    rdwrLock.readLock().lock();
                    rdwrLock.writeLock().unlock();
                }
            }
        } catch (Exception e) {
            logger.error("Error getRegistry ",e);
            throw new EagleFrameException(e);
        } finally {
            rdwrLock.readLock().unlock();
        }
        return center;
    }

    protected abstract RegistryCenter createRegistry(MergeConfig registryConfig);
}
