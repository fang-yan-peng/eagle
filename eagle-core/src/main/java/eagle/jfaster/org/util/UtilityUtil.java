package eagle.jfaster.org.util;

import java.lang.reflect.Constructor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by fangyanpeng1 on 2017/8/3.
 */
public class UtilityUtil {


    public static void quietlySleep(final long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
        }
    }


    public static <T> T createInstance(final String className, final Class<T> clazz, final Object... args) {
        if (className == null) {
            return null;
        }

        try {
            Class<?> loaded = UtilityUtil.class.getClassLoader().loadClass(className);
            if (args.length == 0) {
                return clazz.cast(loaded.newInstance());
            }

            Class<?>[] argClasses = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argClasses[i] = args[i].getClass();
            }
            Constructor<?> constructor = loaded.getConstructor(argClasses);
            return clazz.cast(constructor.newInstance(args));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(final int queueSize, final String threadName, ThreadFactory threadFactory, final RejectedExecutionHandler policy) {
        if (threadFactory == null) {
            threadFactory = new DefaultThreadFactory(threadName, true);
        }

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5, SECONDS, queue, threadFactory, policy);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    public static final class DefaultThreadFactory implements ThreadFactory {

        private final String threadName;
        private final boolean daemon;

        public DefaultThreadFactory(String threadName, boolean daemon) {
            this.threadName = threadName;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, threadName);
            thread.setDaemon(daemon);
            return thread;
        }
    }
}
