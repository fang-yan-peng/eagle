package eagle.jfaster.org.constant;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 常量信息
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */
public class EagleConstants {
    public static final String EAGLE_JFASTER_ORG_SOCKET_SNDBUF_SIZE = "eagle.jfaster.org.socket.sndbuf.size";
    public static final String EAGLE_JFASTER_ORG_SOCKET_RCVBUF_SIZE = "eagle.jfaster.org.socket.rcvbuf.size";
    public static final String DEFAULT_VERSION = "1.0";
    public static final String NODE_TYPE_SERVICE = "service";
    public static final String NODE_TYPE_REFERER = "refer";
    public static final int SECOND_MILLS = 1000;
    public static final int MINUTE_MILLS = 60 * SECOND_MILLS;
    public static final String PROTOCOL_DEFAULT = "eagle";
    public static final String CLUSTER_DEFAULT = "eagle";
    public static final String APPLICATION_DEFAULT = "eagle";
    public static final String MODULE_DEFAULT = "eagle";
    public static final short EAGLE_MAGIC_CODE = (short) 0xDA00;
    public static final short EAGLE_MAGIC_MASK = (short) 0xFF00;
    public static final short EAGLE_TYPE_REQ = (short) 0x0001;
    public static final short EAGLE_COMPRESS_TYPE = (short) 0x0002;
    public static final short EAGLE_RESPONSE_TYPE = (short) 0x000C;
    public static final short EAGLE_RESPONSE_VOID = (short) 0x0000;
    public static final short EAGLE_RESPONSE_NORMAL = (short) 0x0004;
    public static final short EAGLE_RESPONSE_EXCEPTION = (short) 0x0008;
    public static final short EAGLE_REQ_PARAMETER = (short) 0x0010;
    public static final String DEFAULT_CHARACTER = "utf-8";
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    public static final String HEARTBEAT_INTERFACE_NAME = "eagle.jfaster.org.transport.support.EagleHeartBeat";
    public static final String HEARTBEAT_METHOD_NAME = "heartBeat";
    public static int SOCKET_SNDBUF_SIZE = Integer.parseInt(System.getProperty(EAGLE_JFASTER_ORG_SOCKET_SNDBUF_SIZE, "65535"));
    public static int SOCKET_RCVBUF_SIZE = Integer.parseInt(System.getProperty(EAGLE_JFASTER_ORG_SOCKET_RCVBUF_SIZE, "65535"));
    public static final int DEFAULT_WORKER_THREAD = Runtime.getRuntime().availableProcessors();
    public static final int DEFAULT_QUEUE_SIZE = 200;
    public static final int GRUOUP_WORKER_THREAD = Runtime.getRuntime().availableProcessors()*2;
    public static final int NETTY_TIMEOUT_TIMER_PERIOD = 200;
    public static final int ASYNC_TIMEOUT_TIMER_PERIOD = 1000;
    public static final long MAX_LIFETIME = MINUTES.toMillis(30);
    public static final Pattern REGISTRY_SPLIT_PATTERN = Pattern.compile("\\s*[|;]+\\s*");
    public static final Pattern GROUP_SPLIT_PATTERN = Pattern.compile("\\s*[,;]+\\s*");
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    public static final Pattern HOST_SPLIT_PATTERN = Pattern.compile("\\s*[:]+\\s*");
    public static final String RPC_HANDLER = "eagle";
    public static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    public static final String PACKAGE = "base-package";
    public static final int WARMUP = 10 * 60 * 1000;


}
