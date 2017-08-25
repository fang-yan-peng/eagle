package eagle.jfaster.org.util;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author  fangyanpeng
 */
public class AuthenticatUtil {

    private static final String user = "root";

    private static final String AUTH_PREFIX = "Basic ";

    public static void authenticateSuccess(final HttpServletResponse response) {
        response.setStatus(200);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        response.setHeader("identify", "root");
    }

    public static void needAuthenticate(final HttpServletResponse response) {
        response.setStatus(401);
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        response.setHeader("WWW-authenticate", AUTH_PREFIX + "Realm=\"Elastic Job Console Auth\"");
    }
}
