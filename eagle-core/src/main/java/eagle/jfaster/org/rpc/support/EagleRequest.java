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

package eagle.jfaster.org.rpc.support;

import com.google.common.collect.Maps;

import eagle.jfaster.org.rpc.Request;
import lombok.Setter;

import java.util.Map;

/**
 * 默认请求体
 *
 * Created by fangyanpeng1 on 2017/7/28.
 */
public class EagleRequest implements Request {

    @Setter
    private int opaque;

    @Setter
    private String interfaceName;

    @Setter
    private String methodName;

    @Setter
    private String parameterDesc;

    @Setter
    private boolean needCompress = false;

    @Setter
    private Object[] parameters;

    @Setter
    private Map<String, String> attachments;

    @Override
    public int getOpaque() {
        return opaque;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getParameterDesc() {
        return parameterDesc;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public boolean isNeedCompress() {
        return needCompress;
    }

    @Override
    public Map<String, String> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachment(String name, String value) {
        if (attachments == null) {
            attachments = Maps.newHashMap();
        }
        attachments.put(name, value);
    }
}
