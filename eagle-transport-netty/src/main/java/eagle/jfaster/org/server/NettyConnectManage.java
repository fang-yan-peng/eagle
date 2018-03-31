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

package eagle.jfaster.org.server;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

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

    @Getter
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
            RemotingUtil.closeChannel(ctx.channel(), "Connected channel too many");
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
        RemotingUtil.closeChannel(ctx.channel(), "NettyConnectManage exceptionCaught");
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

    public synchronized void close() {
        for (Map.Entry<String, Channel> entry : channels.entrySet()) {
            try {
                RemotingUtil.closeChannel(entry.getValue(), "NettyConnectManage close");
            } catch (Exception e) {
                logger.error("Close NettyConnectManage error ", e);
            }
        }
        channels.clear();
    }
}
