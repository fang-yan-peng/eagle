package eagle.jfaster.org.codec.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import static eagle.jfaster.org.constant.EagleConstants.*;

import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.spi.SpiInfo;
import java.io.IOException;

/**
 *
 *
 * Created by fangyanpeng1 on 2017/7/29.
 */
@SpiInfo(name = "fastjson")
public class FastJsonSerailization implements Serialization {

    @Override
    public byte[] serialize(Object data) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.config(SerializerFeature.WriteClassName, true);
        serializer.write(data);
        return out.toBytes(CHARSET_UTF8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        return JSON.parseObject(new String(data,CHARSET_UTF8), clz);
    }
}
