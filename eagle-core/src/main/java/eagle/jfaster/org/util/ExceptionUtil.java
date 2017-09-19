package eagle.jfaster.org.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常处理工具类.
 *
 * Create by fangyanpeng 2017/09/19
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionUtil {
    
    /**
     * 将Throwable异常转换为字符串.
     *
     * @param cause 需要转换的异常
     * @return 转换后的异常字符串
     */
    public static String transform(final Throwable cause) {
        if (null == cause) {
            return "";
        }
        StringWriter result = new StringWriter();
        try (PrintWriter writer = new PrintWriter(result)) {
            writer.println(cause.getMessage());
            cause.printStackTrace(writer);
        }
        return result.toString();
    }
}
