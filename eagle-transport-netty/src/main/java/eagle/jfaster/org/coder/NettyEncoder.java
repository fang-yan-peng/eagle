package eagle.jfaster.org.coder;

import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.util.RemotingUtil;
import eagle.jfaster.org.codec.Codec;
import eagle.jfaster.org.codec.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import java.nio.ByteBuffer;

/**
 * netty编码器
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */
@RequiredArgsConstructor
public class NettyEncoder extends MessageToByteEncoder {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyEncoder.class);


    private final Codec codec;

    private final Serialization serialization;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf byteBuf) throws Exception {
        try {
            ByteBuffer data = codec.encode(message,serialization);
            byteBuf.writeBytes(data);
        } catch (Throwable e) {
            logger.error("Error encode message "+RemotingUtil.parseChannelRemoteAddr(ctx.channel()),e);
            RemotingUtil.closeChannel(ctx.channel(),"NettyEncoder encode");
            throw new EagleFrameException(e.getMessage());
        }
    }
}
