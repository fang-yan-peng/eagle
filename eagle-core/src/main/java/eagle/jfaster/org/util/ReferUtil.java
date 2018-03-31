/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

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

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!scheduledExecutor.isShutdown()) {
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

