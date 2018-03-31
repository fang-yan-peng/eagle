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

/**
 * Created by fangyanpeng on 2017/12/13.
 */
public class TraceContext {

    public static String TRACE_KEY = "traceId";

    private static InheritableThreadLocal<String> traceIdManager = new InheritableThreadLocal<>();

    public static String getTraceId() {
        return traceIdManager.get();
    }

    public static void setTraceId(String traceId) {
        traceIdManager.set(traceId);
    }

    public static void clear() {
        traceIdManager.remove();
    }


}
