package eagle.jfaster.org.coder;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.util.RemotingUtil;
import eagle.jfaster.org.codec.Codec;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.exception.EagleFrameException;
import static eagle.jfaster.org.util.RequestUtil.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.nio.ByteBuffer;

/**
 * netty解码器
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */
public class NettyDecorder extends LengthFieldBasedFrameDecoder {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(NettyDecorder.class);


    private final Codec codec;

    private final Serialization serialization;

    public NettyDecorder(int maxContentLength,Codec codec,Serialization serialization){
        super(maxContentLength,0,4,-4,4);
        this.codec = codec;
        this.serialization = serialization;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            ByteBuffer byteBuffer = frame.nioBuffer();
            short magicCode = byteBuffer.getShort();
            if(isNotIllegal(magicCode)){
                throw new EagleFrameException("Error the type: %d is not supported",magicCode);
            }
            int opaque = byteBuffer.getInt();
            try {
                return codec.decode(byteBuffer,serialization,opaque,magicCode);
            }catch (Exception e){
                if(isRequest(magicCode)){
                    return buildExceptionResponse(opaque,e);
                }
                logger.error("Error codec decode ",e);
                return null;
            }
        } catch (Exception e){
            logger.error("Error decode message ",e);
            RemotingUtil.closeChannel(ctx.channel(),"NettyDecorder decode");
            return null;
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
    }
}
