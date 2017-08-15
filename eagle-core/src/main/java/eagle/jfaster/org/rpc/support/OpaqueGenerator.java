package eagle.jfaster.org.rpc.support;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求唯一标识
 * Created by fangyanpeng1 on 2017/7/31.
 */
public class OpaqueGenerator {

    private static AtomicInteger opaque = new AtomicInteger(0);

    public static int getOpaque(){
        return opaque.getAndIncrement();
    }
}
