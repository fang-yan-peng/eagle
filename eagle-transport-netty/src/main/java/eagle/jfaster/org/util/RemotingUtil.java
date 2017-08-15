package eagle.jfaster.org.util;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;

import java.net.SocketAddress;

/**
 * netty channel工具类
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */

public class RemotingUtil {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(RemotingUtil.class);

    @Getter
    private static boolean linuxPlatform = false;

    @Getter
    private static boolean windowsPlatform = false;

    public static final String OS_NAME = System.getProperty("os.name");

    static {
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
            linuxPlatform = true;
        }

        if (OS_NAME != null && OS_NAME.toLowerCase().contains("windows")) {
            windowsPlatform = true;
        }
    }

    public static void closeChannel(Channel channel, final String reason) {
        final String addrRemote = parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("closeChannel: close the connection to remote address[{}] result: {},reason: {}", addrRemote, future.isSuccess(),reason);
            }
        });
    }

    public static String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";
        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }
            return addr;
        }
        return "";
    }
}
