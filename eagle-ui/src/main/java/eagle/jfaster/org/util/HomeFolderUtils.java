package eagle.jfaster.org.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * 用户目录工具类.
 * 
 * @author fangyanpeng
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HomeFolderUtils {
    
    private static final String USER_HOME = System.getProperty("user.home");
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private static final String CONSOLE_ROOT_FOLDER = ".elastic-job-console";
    
    /**
     * 获取用户目录文件.
     * 
     * @param fileName 文件名
     * @return 用户目录所在的文件名
     */
    public static String getFilePathInHomeFolder(final String fileName) {
        return String.format("%s%s", getHomeFolder(), fileName);
    }
    
    /**
     * 创建用户目录.
     */
    public static void createHomeFolderIfNotExisted() {
        File file = new File(getHomeFolder());
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    private static String getHomeFolder() {
        return String.format("%s%s%s%s", USER_HOME, FILE_SEPARATOR, CONSOLE_ROOT_FOLDER, FILE_SEPARATOR);
    }
}
