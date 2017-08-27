package eagle.jfaster.org.codec.support;

import com.google.common.base.Strings;
import static eagle.jfaster.org.constant.EagleConstants.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eagle.jfaster.org.codec.Codec;
import eagle.jfaster.org.codec.Serialization;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.rpc.support.EagleRequest;
import eagle.jfaster.org.rpc.support.EagleResponse;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.spi.SpiInfo;
import eagle.jfaster.org.util.ReflectUtil;
import static eagle.jfaster.org.util.RequestUtil.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编解码器
 *
 * 请求协议：magicCode(2个字节) + opaque(4个字节)
 * + interfaceName长度(2个字节) +interfaceName
 * + methodName长度(2个字节) + methodName
 * + parameterDesc长度(4个字节)  + parameterDesc
 * + parameterList(长度+值)
 *
 * 返回协议：magicCode(2个字节) + opaque(4个字节)
 * + className长度(2个字节) + className
 * + value
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
@SpiInfo(name = "eagle")
public class EagleCodec implements Codec {

    private static final byte[] EMPTY_BYTES = new byte[0];

    @Override
    public ByteBuffer encode(Object message, Serialization serialization) throws IOException {
        if(Response.class.isInstance(message)){ //编码response
            try {
                return encodeResponse((Response) message,serialization);
            } catch (Throwable e) {
                return encodeExceptionResponse((Response) message,serialization,e);
            }
        }else {
            return encodeRequest((Request) message,serialization);
        }
    }

    private ByteBuffer encodeExceptionResponse(Response messageRes,Serialization serialization,Throwable e) throws IOException {
        EagleResponse response = new EagleResponse();
        response.setOpaque(messageRes.getOpaque());
        response.setNeedCompress(false);
        response.setException(new EagleFrameException(e.getMessage()));
        return encodeResponse(response,serialization);
    }

    private ByteBuffer encodeResponse(Response response,Serialization serialization) throws IOException {
        short magicCode = EAGLE_MAGIC_CODE;
        if(response.isNeedCompress()){
            magicCode |= EAGLE_COMPRESS_TYPE;
        }
        int dataLen = 10;//totalLen+magic+opaque 4+2+4
        ByteBuffer content;
        if(response.getException() != null){
            content = encodeResponseCommon(response.getException(),dataLen,magicCode,response.getOpaque(),serialization,false,EAGLE_RESPONSE_EXCEPTION);
        }else if(response.getValue() != null){
            content = encodeResponseCommon(response.getValue(),dataLen,magicCode,response.getOpaque(),serialization,response.isNeedCompress(),EAGLE_RESPONSE_NORMAL);
        }else {
            content = ByteBuffer.allocate(dataLen);
            content.putInt(dataLen);
            content.putShort(magicCode);
            content.putInt(response.getOpaque());
        }
        content.flip();
        return content;

    }

    private ByteBuffer encodeResponseCommon(Object res,int dataLen,short magicCode,int opaque,Serialization serialization,boolean compress,short flag) throws IOException {
        magicCode |= flag;
        String className = res.getClass().getName();
        byte[] nameData = className.getBytes(CHARSET_UTF8);
        dataLen += nameData.length + 2;
        byte[] valData = serialization.serialize(res);
        int originLen = valData.length;
        if(compress){
            valData = compress(valData);
            dataLen += 4;
        }
        dataLen += valData.length + 4;
        ByteBuffer content = ByteBuffer.allocate(dataLen);
        content.putInt(dataLen);
        content.putShort(magicCode);
        content.putInt(opaque);
        content.putShort((short) nameData.length);
        content.put(nameData);
        content.putInt(valData.length);
        content.put(valData);
        if(compress){
            content.putInt(originLen);
        }
        return content;
    }

    private ByteBuffer encodeRequest(Request request,Serialization serialization) throws IOException {
        short magicCode = EAGLE_MAGIC_CODE;
        magicCode |= EAGLE_TYPE_REQ;
        if(request.isNeedCompress()){
            magicCode |= EAGLE_COMPRESS_TYPE;
        }
        int dataLen = 14;//totalLen+magic+opaque+attachments 4+2+4+4
        String interfaceName = request.getInterfaceName();
        byte[] iNameData = interfaceName.getBytes(CHARSET_UTF8);
        dataLen += iNameData.length + 2;
        String mName = request.getMethodName();
        byte[] mNameData = mName.getBytes(CHARSET_UTF8);
        dataLen += mNameData.length + 2;
        ByteBuffer content;
        byte[] extFieldsBytes = null;
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            extFieldsBytes = encodeAttachments(request.getAttachments());
            dataLen += extFieldsBytes.length;
        }
        if(!Strings.isNullOrEmpty(request.getParameterDesc())){
            magicCode |= EAGLE_REQ_PARAMETER;
            String paramDesc = request.getParameterDesc();
            byte[] paramDescData = paramDesc.getBytes(CHARSET_UTF8);
            dataLen += paramDescData.length + 4;
            Object[] params = request.getParameters();
            List<byte[]> paramsData = Lists.newArrayListWithExpectedSize(8);
            for(Object param : params){
                byte[] data = EMPTY_BYTES;
                if(param != null) {
                    data = serialization.serialize(param);
                }
                dataLen += data.length + 4;
                paramsData.add(data);

            }
            content = encodeReqCommon(dataLen,magicCode,request.getOpaque(),iNameData,mNameData);
            content.putInt(paramDescData.length);
            content.put(paramDescData);
            for(byte[] data : paramsData){
                content.putInt(data.length);
                content.put(data);
            }
        }else {
            content = encodeReqCommon(dataLen,magicCode,request.getOpaque(),iNameData,mNameData);
        }
        if(extFieldsBytes != null){
            content.putInt(extFieldsBytes.length);
            content.put(extFieldsBytes);
        }else {
            content.putInt(0);
        }
        content.flip();
        return  content;
    }

    private  ByteBuffer encodeReqCommon(int dataLen,short magicCode,int opaque,byte[] iNameData,byte[] mNameData){
        ByteBuffer content = ByteBuffer.allocate(dataLen);
        content.putInt(dataLen);
        content.putShort(magicCode);
        content.putInt(opaque);
        content.putShort((short) iNameData.length);
        content.put(iNameData);
        content.putShort((short) mNameData.length);
        content.put(mNameData);
        return content;
    }

    public static byte[] encodeAttachments(Map<String, String> map) {
        int totalLength = 0;
        int kvLength;
        Map<byte[],byte[]> byteMap = Maps.newHashMapWithExpectedSize(map.size());
        byte[] key;
        byte[] val;
        for(Map.Entry<String,String> entry : map.entrySet()){
            if (entry.getKey() != null && entry.getValue() != null) {
                key = entry.getKey().getBytes(CHARSET_UTF8);
                val = entry.getValue().getBytes(CHARSET_UTF8);
                kvLength =
                        // keySize + Key
                        2 + key.length
                                // valSize + val
                                + 4 + val.length;
                totalLength += kvLength;
                byteMap.put(key,val);
            }
        }

        ByteBuffer content = ByteBuffer.allocate(totalLength);
        for(Map.Entry<byte[],byte[]> entry : byteMap.entrySet()){
                key = entry.getKey();
                val = entry.getValue();

                content.putShort((short) key.length);
                content.put(key);

                content.putInt(entry.getValue().length);
                content.put(val);

        }

        return content.array();
    }

    @Override
    public Object decode(ByteBuffer buffer, Serialization serialization, int opaque, short magicCode) throws IOException {
        try {
            if(isRequest(magicCode)){
                return decodeRequest(buffer,serialization,opaque,magicCode);
            }else {
                return decodeResponse(buffer,serialization,opaque,magicCode);
            }
        } catch (Exception e) {
            throw new EagleFrameException(e.getMessage());
        }
    }

    private Object decodeRequest(ByteBuffer buffer,Serialization serialization,int opaque,short magicCode)
            throws IOException, ClassNotFoundException {
        EagleRequest request = new EagleRequest();
        request.setOpaque(opaque);
        request.setNeedCompress(isCompress(magicCode));

        /*解码interfaceName最大长度127*/
        short len = buffer.getShort();
        byte[] data = new byte[len];
        buffer.get(data);
        request.setInterfaceName(new String(data,CHARSET_UTF8));

        /*解码methodName最大长度127*/
        len = buffer.getShort();
        data = new byte[len];
        buffer.get(data);
        request.setMethodName(new String(data,CHARSET_UTF8));

         /*解码方法参数*/
        if(isRequestWithParameter(magicCode)){
            // 方法参数描述
            int paramDescLen = buffer.getInt();
            byte[] paramDescData  = new byte[paramDescLen];
            buffer.get(paramDescData);
            String paramDesc = new String(paramDescData,CHARSET_UTF8);
            request.setParameterDesc(paramDesc);
            request.setParameters(decodeRequestParameter(buffer,paramDesc,serialization));
        }
        request.setAttachments(decodeRequestAttachments(buffer));
        return request;
    }

    private Object[] decodeRequestParameter(ByteBuffer buffer, String parameterDesc, Serialization serialization) throws IOException,
            ClassNotFoundException {
        if (Strings.isNullOrEmpty(parameterDesc)) {
            return null;
        }
        Class<?>[] classTypes = ReflectUtil.forNames(parameterDesc);
        Object[] paramObjs = new Object[classTypes.length];
        int dataLen;
        byte[] data;
        for (int i = 0; i < classTypes.length; i++) {
            dataLen = buffer.getInt();
            if(dataLen != 0){
                data = new byte[dataLen];
                buffer.get(data);
                paramObjs[i] = serialization.deserialize(data,classTypes[i]);
            }
        }

        return paramObjs;
    }

    private Map<String, String> decodeRequestAttachments(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        int size = buffer.getInt();
        if (size <= 0) {
            return null;
        }
        byte[] data = new byte[size];
        ByteBuffer attachBuffer = ByteBuffer.wrap(data);
        short keySize;
        byte[] keyContent;
        int valSize;
        byte[] valContent;
        Map<String, String> attachments = new HashMap();
        while (attachBuffer.hasRemaining()){
            keySize = attachBuffer.getShort();
            keyContent = new byte[keySize];
            attachBuffer.get(keyContent);

            valSize = attachBuffer.getInt();
            valContent = new byte[valSize];
            attachBuffer.get(valContent);

            attachments.put(new String(keyContent, CHARSET_UTF8), new String(valContent, CHARSET_UTF8));
        }
        return attachments;
    }

    public Object decodeResponse(ByteBuffer buffer, Serialization serialization, int opaque, short magicCode)
            throws ClassNotFoundException, IOException {
        EagleResponse response = new EagleResponse();
        response.setOpaque(opaque);
        if(isVoidValue(magicCode)){
            return response;
        }
        //解析返回的类型
        short len = buffer.getShort();
        byte[] data = new byte[len];
        buffer.get(data);
        String className = new String(data,CHARSET_UTF8);
        Class<?> clz = ReflectUtil.forName(className);
        //解析值
        int valLen = buffer.getInt();
        data = new byte[valLen];
        buffer.get(data);
        Object val;
        if(isCompress(magicCode)){//需要对内容进行解压
            data = unCompress(data,buffer.getInt());
        }
        val = serialization.deserialize(data,clz);
        if(isNormalValue(magicCode)){
            response.setValue(val);
        }else {
            response.setException((Exception) val);
        }
        return response;
    }
}
