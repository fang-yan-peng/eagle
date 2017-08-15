package eagle.jfaster.org.codec.support;

import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.spi.SpiInfo;
import hprose.io.ByteBufferStream;
import hprose.io.HproseReader;
import hprose.io.HproseWriter;

import java.io.IOException;

/**
 * hprose 序列化，不要求序列化的对象实现 java.io.Serializable 接口，
 * 但序列化的字段需要是 public 的，或者定义有 public 的 setter 和 getter 方法。
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
@SpiInfo(name = "hprose")
public class HproseSerialization implements Serialization {

    @Override
    public byte[] serialize(Object data) throws IOException {
        ByteBufferStream stream = null;
        try {
            stream = new ByteBufferStream();
            HproseWriter writer = new HproseWriter(stream.getOutputStream());
            writer.serialize(data);
            byte[] result = stream.toArray();
            return result;
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        return new HproseReader(data).unserialize(clz);
    }
}

