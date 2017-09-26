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

import com.google.common.base.Throwables;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class Responses {

    private Responses(){}

    /**
     * write json data to response
     */
    public static boolean writeJson(HttpServletResponse resp, Object msg){
        return write(resp, "application/json", msg);
    }

    /**
     * write text data to response
     */
    public static boolean writeText(HttpServletResponse resp, Object msg){
        return write(resp, "text/plain", msg);
    }

    public static boolean write(HttpServletResponse resp, String contentType, Object msg){
        resp.setStatus(200);
        resp.setContentType(contentType);
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            out.print(msg);
            out.flush();
            return true;
        } catch (Exception e) {
            Logs.error("failed to writeJson response : {}", Throwables.getStackTraceAsString(e));
            return false;
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }

    public static void disableCache(HttpServletResponse resp){
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.setHeader("Cache-Control", "no-cache,no-store");
    }
}
