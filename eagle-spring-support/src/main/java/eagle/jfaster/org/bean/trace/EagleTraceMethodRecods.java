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
package eagle.jfaster.org.bean.trace;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangyanpeng on 2017/12/16.
 */
public class EagleTraceMethodRecods {

    private static Map<MethodCacheKey,Boolean> traces = new HashMap<>();

    public static boolean needTrace(Method method,Class<?> targetClass){
        Boolean trace = traces.get(new MethodCacheKey(method,targetClass));
        if(trace == null){
            return false;
        }
        return trace;
    }

    public static synchronized void recordTrace(Method method,Class<?> targetClass,boolean trace){
        traces.put(new MethodCacheKey(method,targetClass),trace);
    }


}
