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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fangyanpeng
 */
public final class Logs {

    private static final Logger INFO = LoggerFactory.getLogger("info");

    private static final Logger WARN = LoggerFactory.getLogger("warn");

    private static final Logger NOTIFY = LoggerFactory.getLogger("notify");

    private static final Logger ERROR = LoggerFactory.getLogger("error");

    private Logs(){}

    public static void info(String msg, Object... args){
        INFO.info(msg, args);
    }

    public static void warn(String msg, Object... args){
        WARN.warn(msg, args);
    }

    public static void error(String msg, Object... args){
        ERROR.error(msg, args);
    }

    public static void nofity(String msg, Object... args){
        NOTIFY.info(msg, args);
    }
}
