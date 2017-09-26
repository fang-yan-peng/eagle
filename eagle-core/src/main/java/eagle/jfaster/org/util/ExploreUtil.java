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

import eagle.jfaster.org.config.ConfigEnum;
import eagle.jfaster.org.config.common.MergeConfig;
import eagle.jfaster.org.rpc.Request;

/**
 *
 * Created by fangyanpeng1 on 2017/7/31.
 */
public class ExploreUtil {

    public static final String SERVICE_KEY_FORMAT = "%s-%s";

    public static String getServiceKey(MergeConfig config){
        return String.format(SERVICE_KEY_FORMAT,config.getInterfaceName(),config.getVersion());
    }

    public static String getServiceKey(Request request){
        return String.format(SERVICE_KEY_FORMAT,request.getInterfaceName(),getVersion(request));
    }

    public static String getVersion(Request request){
        String version = ConfigEnum.version.getValue();
        if(request.getAttachments() != null && request.getAttachments().containsKey(ConfigEnum.version.name())){
            request.getAttachments().get(ConfigEnum.version.name());
        }
        return version;
    }
}
