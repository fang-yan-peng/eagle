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

package eagle.jfaster.org.container;

import eagle.jfaster.org.constant.EagleConstants;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.spi.SpiClassLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fangyanpeng1 on 2017/8/14.
 */
public class Main {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Main.class);

    public static final String CONTAINER_KEY = "eagle.container";

    public static final String DEFAULT_CONTAINER_KEY = "spring";

    public static final String SHUTDOWN_HOOK_KEY = "eagle.shutdown.hook";

    private static volatile boolean running = true;


    public static void main(String[] args) {
        try {
            if (args == null || args.length == 0) {
                String config = System.getProperty(CONTAINER_KEY, DEFAULT_CONTAINER_KEY);
                args = EagleConstants.COMMA_SPLIT_PATTERN.split(config);
            }

            final List<Container> containers = new ArrayList<Container>(args.length);
            for (int i = 0; i < args.length; i++) {
                containers.add(SpiClassLoader.getClassLoader(Container.class).getExtension(args[i]));
            }
            logger.info("Use container type(" + Arrays.toString(args) + ") to run eagle serivce.");

            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY))) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        for (Container container : containers) {
                            try {
                                container.stop();
                                logger.info("eagle " + container.getClass().getSimpleName() + " stopped!");
                            } catch (Throwable t) {
                                logger.error(t.getMessage(), t);
                            }
                            synchronized (Main.class) {
                                running = false;
                                Main.class.notify();
                            }
                        }
                    }
                });
            }

            for (Container container : containers) {
                container.start();
                logger.info("eagle " + container.getClass().getSimpleName() + " started!");
            }
            System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " eagle service server started!");
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
        synchronized (Main.class) {
            while (running) {
                try {
                    Main.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }
}
