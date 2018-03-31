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

package eagle.jfaster.org.rpc.support;

import com.google.common.base.Strings;

import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import sun.swing.SwingUtilities2;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import static eagle.jfaster.org.util.IpUtil.getIp;
import static eagle.jfaster.org.util.IpUtil.isIllegalIp;
import static eagle.jfaster.org.util.PidUtil.getPid;

/**
 * 请求唯一标识
 * Created by fangyanpeng1 on 2017/7/31.
 */
public class OpaqueGenerator {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpaqueGenerator.class);

    private static AtomicInteger opaque = new AtomicInteger(0);

    private static DistributeIdGenerator generator = new DistributeIdGenerator();

    private static long machineId;/*ip地址 + 进程号*/

    static {
        String ip = System.getProperty("host");
        if (Strings.isNullOrEmpty(ip)) {
            ip = getIp();
        } else if (!isIllegalIp(ip)) {
            throw new EagleFrameException("Input ip: '%s' is not illegal", ip);
        }
        int pid = getPid();
        logger.info(String.format("Generate distribute id depends on ip: '%s' and pid: '%d'", ip, pid));
        String[] segments = ip.split("\\.");
        machineId |= Long.parseLong(segments[0]) << 56;
        machineId |= Long.parseLong(segments[1]) << 48;
        machineId |= Long.parseLong(segments[2]) << 40;
        machineId |= Long.parseLong(segments[3]) << 32;
        machineId |= pid;

    }

    public static int getOpaque() {
        return opaque.getAndIncrement();
    }

    public static String getDistributeOpaque() {
        return String.format("%016x%016x", machineId, generator.nextId());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 30; ++i) {
            String id = getDistributeOpaque();
            System.out.println(id);
            System.out.println(id.getBytes(Charset.forName("UTF-8")).length);
            System.out.println(new String(id.getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")));
            System.out.println("=================");

        }
    }
}
