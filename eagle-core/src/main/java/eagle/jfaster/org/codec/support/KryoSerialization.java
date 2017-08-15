package eagle.jfaster.org.codec.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.spi.SpiInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
@SpiInfo(name = "kryo")
public class KryoSerialization implements Serialization {

    @Override
    public byte[] serialize(Object obj) throws IOException {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        //解决exception序列化问题 https://github.com/magro/kryo-serializers
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        Output out = new Output(byteOut);
        kryo.writeObject(out,obj);
        out.flush();
        return byteOut.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        //解决exception序列化问题
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        Input in = new Input(new ByteArrayInputStream(bytes));
        return kryo.readObject(in,clz);
    }
}
