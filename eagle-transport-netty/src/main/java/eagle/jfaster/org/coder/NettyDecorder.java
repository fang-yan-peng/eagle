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

package eagle.jfaster.org.coder;

import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.support.EagleResponse;
import eagle.jfaster.org.codec.Codec;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.exception.EagleFrameException;

import static eagle.jfaster.org.constant.EagleConstants.CHARSET_UTF8;
import static eagle.jfaster.org.util.RequestUtil.*;

import eagle.jfaster.org.util.ExceptionUtil;
import eagle.jfaster.org.util.RemotingUtil;
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
                throw new EagleFrameException("Error the type: '%d' is not supported",magicCode);
            }
            int opaque = byteBuffer.getInt();
            try {
                return codec.decode(byteBuffer,serialization,opaque,magicCode);
            }catch (Throwable e){
                logger.error("Error codec decode ",e);
                EagleResponse response;
                if(e instanceof EagleFrameException){
                    response = buildExceptionResponse(opaque,(Exception)e);
                }else{
                    response = buildExceptionResponse(opaque,new EagleFrameException(e.getMessage()));
                }
                if(isRequest(magicCode)){
                    ctx.writeAndFlush(response);
                    return null;
                }else{
                    return response;
                }
            }
        } catch (Throwable e){
            logger.error("Error decode message ",e);
            RemotingUtil.closeChannel(ctx.channel(),"NettyEncoder decode");
            throw ExceptionUtil.handleException(e);
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
    }
}
