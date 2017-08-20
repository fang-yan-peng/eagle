package eagle.jfaster.org.cluster.ha;

import eagle.jfaster.org.cluster.LoadBalance;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Refer;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.spi.SpiInfo;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
@SpiInfo(name = "failover")
public class FailoverHaStrategy<T> extends AbstractHaStrategy<T>  {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(FailoverHaStrategy.class);

    @Override
    public Object call(Request request, LoadBalance<T> loadBalance) {
        int retry = config.getExtInt(ConfigEnum.retries.getName(),ConfigEnum.retries.getIntValue());
        retry = retry < 0 ? 1 : retry;
        for(int i = 0;i <= retry ;++i){
            Refer<T> refer = loadBalance.select(request);
            try {
                return refer.request(request);
            } catch (Exception e) {
                if(i > retry){
                    throw e;
                }
                logger.warn("Failover.call fail for interface:%s,cause:%s",request.getInterfaceName(),e.getMessage());
            }
        }
        throw new EagleFrameException("Failover.call can'nt run here!");
    }
}
