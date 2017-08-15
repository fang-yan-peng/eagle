package eagle.jfaster.org.codec.support;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.spi.SpiInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * hession2 序列化，要求序列化的对象实现 java.io.Serializable 接口
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
@SpiInfo(name="hessian")
public class HessianSerialization implements Serialization {
    @Override
    public byte[] serialize(Object data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(bos);
        out.writeObject(data);
        out.flush();
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        return (T) input.readObject(clz);
    }
}
