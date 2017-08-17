package eagle.jfaster.org.codec.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;
import de.javakaffee.kryoserializers.guava.*;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.spi.SpiInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
@SpiInfo(name = "kryo")
public class KryoSerialization implements Serialization {

    protected static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register( CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer());
            UnmodifiableCollectionsSerializer.registerSerializers(kryo);
            SynchronizedCollectionsSerializer.registerSerializers( kryo );
            kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
            ImmutableListSerializer.registerSerializers( kryo );
            ImmutableSetSerializer.registerSerializers( kryo );
            ImmutableMapSerializer.registerSerializers( kryo );
            ImmutableMultimapSerializer.registerSerializers( kryo );
            ReverseListSerializer.registerSerializers( kryo );
            UnmodifiableNavigableSetSerializer.registerSerializers( kryo );
            ArrayListMultimapSerializer.registerSerializers( kryo );
            HashMultimapSerializer.registerSerializers( kryo );
            LinkedHashMultimapSerializer.registerSerializers( kryo );
            LinkedListMultimapSerializer.registerSerializers( kryo );
            TreeMultimapSerializer.registerSerializers( kryo );
            return kryo;
        }
    };

    @Override
    public byte[] serialize(Object obj) throws IOException {
        Kryo kryo = kryos.get();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        Output out = new Output(byteOut);
        kryo.writeClassAndObject(out,obj);
        out.flush();
        return byteOut.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        Kryo kryo = kryos.get();
        Input in = new Input(new ByteArrayInputStream(bytes));
        return (T) kryo.readClassAndObject(in);
    }
}
