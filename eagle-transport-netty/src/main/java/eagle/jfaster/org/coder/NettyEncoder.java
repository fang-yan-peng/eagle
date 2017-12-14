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

import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.util.ExceptionUtil;
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
            String opaque = null;
            if(message instanceof Request){
                opaque = ((Request) message).getOpaque();
            }else if(message instanceof Response){
                opaque = ((Response) message).getOpaque();
            }
            logger.error(opaque + " Error encode message "+RemotingUtil.parseChannelRemoteAddr(ctx.channel()),e);
            RemotingUtil.closeChannel(ctx.channel(),"NettyEncoder encode");
            throw ExceptionUtil.handleException(e);
        }
    }
}
