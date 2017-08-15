package eagle.jfaster.org.codec.support;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.MessageLite;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.spi.SpiInfo;
import java.io.*;
import java.lang.reflect.Method;

/**
 * protobuf序列化器,支持基本数据类型及其包装类、String、Throwable、Protobuf2/3对象
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
@SpiInfo(name = "protobuf")
public class ProtoBufSerialization implements Serialization {

    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(baos);
        output.writeBoolNoTag(obj == null);
        if (obj == null) {
            output.flush();
            return baos.toByteArray();
        }

        Class<?> clazz = obj.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            output.writeSInt32NoTag((Integer) obj);
        } else if (clazz == long.class || clazz == Long.class) {
            output.writeSInt64NoTag((Long) obj);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            output.writeBoolNoTag((Boolean) obj);
        } else if (clazz == byte.class || clazz == Byte.class) {
            output.writeRawByte((Byte) obj);
        } else if (clazz == char.class || clazz == Character.class) {
            output.writeSInt32NoTag((Character) obj);
        } else if (clazz == short.class || clazz == Short.class) {
            output.writeSInt32NoTag((Short) obj);
        } else if (clazz == double.class || clazz == Double.class) {
            output.writeDoubleNoTag((Double) obj);
        } else if (clazz == float.class || clazz == Float.class) {
            output.writeFloatNoTag((Float) obj);
        } else if (clazz == String.class) {
            output.writeStringNoTag(obj.toString());
        } else if (MessageLite.class.isAssignableFrom(clazz)) {
            output.writeMessageNoTag((MessageLite) obj);
        } else if (Throwable.class.isAssignableFrom(clazz)) {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
        } else {
            throw new IllegalArgumentException("can't serialize " + clazz);
        }

        output.flush();
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        if (bytes[0] == 1)
            return null;
        Object value = null;
        CodedInputStream in = CodedInputStream.newInstance(bytes, 1, bytes.length - 1);
        if (clazz == int.class || clazz == Integer.class) {
            value = in.readSInt32();
        } else if (clazz == long.class || clazz == Long.class) {
            value = in.readSInt64();
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            value = in.readBool();
        } else if (clazz == byte.class || clazz == Byte.class) {
            value = in.readRawByte();
        } else if (clazz == char.class || clazz == Character.class) {
            value = (char) in.readSInt32();
        } else if (clazz == short.class || clazz == Short.class) {
            value = (short) in.readSInt32();
        } else if (clazz == double.class || clazz == Double.class) {
            value = in.readDouble();
        } else if (clazz == float.class || clazz == Float.class) {
            value = in.readFloat();
        } else if (clazz == String.class) {
            value = in.readString();
        } else if (MessageLite.class.isAssignableFrom(clazz)) {
            try {
                Method method = clazz.getDeclaredMethod("newBuilder");
                MessageLite.Builder builder = (MessageLite.Builder) method.invoke(null);
                in.readMessage(builder, null);
                value = builder.build();
            } catch (Exception e) {
                throw new EagleFrameException(e);
            }
        } else if (Throwable.class.isAssignableFrom(clazz)) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes, 0, bytes.length - 1));
                value = ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new EagleFrameException(e);
            }
        } else {
            throw new IllegalArgumentException("can't deserialize " + clazz);
        }

        return (T) value;
    }

}

