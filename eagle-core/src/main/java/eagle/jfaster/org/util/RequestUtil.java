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

package eagle.jfaster.org.util;
import com.google.common.base.Strings;
import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.exception.EagleFrameException;
import eagle.jfaster.org.rpc.Request;
import eagle.jfaster.org.rpc.Response;
import eagle.jfaster.org.rpc.support.EagleResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static eagle.jfaster.org.constant.EagleConstants.*;
/**
 * 对请求和回复体进行预处理
 *
 * Created by fangyanpeng1 on 2017/7/27.
 */
public class RequestUtil {

    private static final String REQ_FORMAT="%s.%s";

    private static final String SERVICE_FORMAT="%s:%s";

    public static boolean isRequest(short magicCode){
        return (magicCode & EAGLE_TYPE_REQ) == EAGLE_TYPE_REQ;
    }

    public static  boolean isRequestWithParameter(short magicCode){
        return (magicCode & EAGLE_REQ_PARAMETER) == EAGLE_REQ_PARAMETER;
    }

    public static boolean isCompress(short magicCode){
        return (magicCode & EAGLE_COMPRESS_TYPE) == EAGLE_COMPRESS_TYPE;
    }

    public static boolean  isNotIllegal(short magicCode){
        return (magicCode & EAGLE_MAGIC_MASK) != EAGLE_MAGIC_CODE;
    }

    public static boolean  isNormalValue(short magicCode){
        return (magicCode & EAGLE_RESPONSE_TYPE) == EAGLE_RESPONSE_NORMAL;
    }

    public static boolean  isVoidValue(short magicCode){
        return (magicCode & EAGLE_RESPONSE_TYPE) == EAGLE_RESPONSE_VOID;
    }

    public static EagleResponse buildExceptionResponse(int opaque,Exception e){
        EagleResponse response = new EagleResponse();
        response.setOpaque(opaque);
        response.setException(e);
        return response;
    }

    public static Response buildRejectResponse(String info){
        EagleResponse response = new EagleResponse();
        response.setException(new EagleFrameException(info));
        return response;
    }


    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(out);
        gos.write(data);
        gos.finish();
        gos.flush();
        gos.close();
        byte[] ret = out.toByteArray();
        return ret;
    }

    public static byte[] unCompress(byte[] data,int len) throws IOException {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
        byte[] ret = new byte[len];
        gis.read(ret);
        gis.close();
        return ret;
    }

    public static String getRequestDesc(Request request){
        return String.format(REQ_FORMAT,request.getInterfaceName(),ReflectUtil.getMethodDesc(request.getMethodName(),request.getParameterDesc()));
    }

    public static String getServiceKey(String interfaceName,String version){
        if(Strings.isNullOrEmpty(version)){
            version = ConfigEnum.version.getValue();
        }
        return String.format(SERVICE_FORMAT,interfaceName,version);
    }

    public static String getServiceKey(String interfaceName,Map<String,String> attachments){
        String version;
        if(CollectionUtil.isEmpty(attachments) || Strings.isNullOrEmpty(version = attachments.get(ConfigEnum.version.getName()))){
            version = ConfigEnum.version.getValue();
        }
        return String.format(SERVICE_FORMAT,interfaceName,version);
    }

}
