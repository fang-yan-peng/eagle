package eagle.jfaster.org.codec;

import eagle.jfaster.org.spi.Scope;
import eagle.jfaster.org.spi.Spi;
import java.io.IOException;

/**
 * Created by fangyanpeng1 on 2017/7/28.
 */
@Spi(scope= Scope.PROTOTYPE)
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException;
}

