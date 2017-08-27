package eagle.jfaster.org.protocol;

import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.pool.SuspendResumeLock;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.statistic.EagleStatsManager;
import eagle.jfaster.org.transport.Client;
import eagle.jfaster.org.util.ClockSource;
import eagle.jfaster.org.util.ReflectUtil;

/**
 *
 * 带统计信息的refer
 *
 * Created by fangyanpeng on 2017/8/22.
 */
public class StatsNettyRefer<T> extends NettyRefer<T> {

    private final String statsKey;

    public StatsNettyRefer(Client client, MergeConfig config, Class<T> type, SuspendResumeLock lock,InternalLogger log) {
        super(client, config, type, lock);
        statsKey = config.identity();
        EagleStatsManager.registerStatsItem(statsKey,log);
    }

    @Override
    public Object request(Request request) {
        try {
            if(lock.tryAcquire()){
                long start = ClockSource.MILLINSTANCE.currentTime();
                try {
                    activeCnt.incrementAndGet();
                    return client.request(request);
                } finally {
                    lock.release();
                    EagleStatsManager.incInvoke(statsKey, ReflectUtil.getMethodDesc(request.getMethodName(),request.getParameterDesc()), ClockSource.MILLINSTANCE.elapsedMillis(start));
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
}
