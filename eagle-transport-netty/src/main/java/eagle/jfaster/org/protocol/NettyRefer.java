package eagle.jfaster.org.protocol;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.pool.SuspendResumeLock;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.transport.Client;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
@RequiredArgsConstructor
public class NettyRefer <T> implements Refer <T> {

    protected final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyRefer.class);

    protected final Client client;

    protected final MergeConfig config;

    private final Class<T> type;

    protected final SuspendResumeLock lock;

    protected AtomicInteger activeCnt = new AtomicInteger(0);

    @Override
    public MergeConfig getConfig() {
        return config;
    }

    @Override
    public int getActiveCount() {
        return activeCnt.get();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public void updateConfig(MergeConfig refConfig) {
        if(config != null){
            config.update(refConfig);
        }
    }

    @Override
    public void close() {
        client.shutdown();
    }

    @Override
    public void init() {
        client.start();
    }

    @Override
    public Object request(Request request) {
        try {
            if(lock.tryAcquire()){
                try {
                    activeCnt.incrementAndGet();
                    return client.request(request);
                } finally {
                    lock.release();
                }
            }else {
                String warn = String.format("%s too much request,more than actives:%d",config.identity(),lock.getMaxPermits());
                logger.warn(warn);
                throw new EagleFrameException(warn);
            }
        } catch (Throwable e) {
            throw new EagleFrameException("NettyRefer request failed,refer:%s,host:%s,cause:%s",config.getInterfaceName(),config.identity(),e.getMessage());
        }finally {
            activeCnt.decrementAndGet();
        }
    }

    @Override
    public boolean isAlive() {
        return client.isAlive();
    }
}
