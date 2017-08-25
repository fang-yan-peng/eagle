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
