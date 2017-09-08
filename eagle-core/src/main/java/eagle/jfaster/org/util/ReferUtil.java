package eagle.jfaster.org.util;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Refer;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fangyanpeng1 on 2017/8/4.
 */
public class ReferUtil {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(ReferUtil.class);

    private static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);

    private static final int DELAY_TIME = 1000;
    static{
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                if(!scheduledExecutor.isShutdown()){
                    scheduledExecutor.shutdownNow();
                }
            }
        });
    }
    public static <T> void delayDestroy(final List<Refer<T>> refers) {
        if (refers == null || refers.isEmpty()) {
            return;
        }

        scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {

                for (Refer<?> refer : refers) {
                    try {
                        refer.close(false);
                    } catch (Exception e) {
                        logger.error("ReferSupports delayDestroy Error: url=" + refer.getConfig().identity(), e);
                    }
                }
            }
        }, DELAY_TIME, TimeUnit.MILLISECONDS);

        logger.info("ReferSupports delayDestroy Success: size={} service={} urls={}", refers.size(), refers.get(0).getConfig().getInterfaceName(), getServerPorts(refers));
    }

    private static <T> String getServerPorts(List<Refer<T>> refers) {
        if (refers == null || refers.isEmpty()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Refer<T> refer : refers) {
            builder.append(refer.getConfig().hostPort()).append(",");
        }
        builder.setLength(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }
}

