package eagle.jfaster.org.server;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * netty 连接数管理，超过配置的数量拒绝连接
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
@ChannelHandler.Sharable
public class NettyConnectManage extends ChannelDuplexHandler {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyConnectManage.class);


    private ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

    private int maxChannel = 0;

    public NettyConnectManage(int maxChannel) {
        super();
        this.maxChannel = maxChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String channelKey = getChannelKey((InetSocketAddress) channel.localAddress(), (InetSocketAddress) channel.remoteAddress());
        if (channels.size() > maxChannel) {
            // 超过最大连接数限制，直接close连接
            logger.warn("Connected channel size out of limit: limit={} current={}", maxChannel, channels.size());
            RemotingUtil.closeChannel(ctx.channel(),"Connected channel too many");
        } else {
            channels.put(channelKey, channel);
            super.channelActive(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String channelKey = getChannelKey((InetSocketAddress) channel.localAddress(), (InetSocketAddress) channel.remoteAddress());
        channels.remove(channelKey);
        super.channelInactive(ctx);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String channelKey = getChannelKey((InetSocketAddress) channel.localAddress(), (InetSocketAddress) channel.remoteAddress());
        channels.remove(channelKey);
        RemotingUtil.closeChannel(ctx.channel(),"NettyConnectManage exceptionCaught");
    }

    private String getChannelKey(InetSocketAddress local, InetSocketAddress remote) {
        String key = "";
        if (local == null || local.getAddress() == null) {
            key += "null-";
        } else {
            key += local.getAddress().getHostAddress() + ":" + local.getPort() + "-";
        }

        if (remote == null || remote.getAddress() == null) {
            key += "null";
        } else {
            key += remote.getAddress().getHostAddress() + ":" + remote.getPort();
        }

        return key;
    }

    public synchronized void close(){
        for(Map.Entry<String,Channel> entry : channels.entrySet()){
            try {
                RemotingUtil.closeChannel(entry.getValue(),"NettyConnectManage close");
            } catch (Exception e) {
                logger.error("Close NettyConnectManage error ",e);
            }
        }
        channels.clear();
    }
}
