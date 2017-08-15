package eagle.jfaster.org.codec;

import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by fangyanpeng1 on 2017/7/28.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface Codec {

    ByteBuffer encode(Object message,Serialization serialization) throws IOException;

    Object decode(ByteBuffer buffer,Serialization serialization,int opaque,short magicCode) throws IOException;
}
