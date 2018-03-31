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

package eagle.jfaster.org.interceptor.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangyanpeng on 2018/3/31.
 */
public class CurrentExecutionContext {

    private static final ThreadLocal<CurrentExecutionContext> currentContext = new ThreadLocal<>();

    private Map executeVariables = new HashMap<>();

    public static void setVariable(Object key, Object value) {
        CurrentExecutionContext context = currentContext.get();
        if (context == null) {
            context = new CurrentExecutionContext();
            setContext(context);
        }
        context.executeVariables.put(key, value);
    }

    public static Object getVariable(Object key) {
        CurrentExecutionContext context = currentContext.get();
        if (context == null) {
            return null;
        }
        return context.executeVariables.get(key);
    }

    public static void clean() {
        currentContext.remove();
    }

    public static CurrentExecutionContext getContext() {
        return currentContext.get();
    }

    public static void setContext(CurrentExecutionContext context) {
        if (context == null) {
            return;
        }
        currentContext.set(context);
    }

}
