package eagle.jfaster.org.logging.plugin;

import eagle.jfaster.org.logging.EagleLogger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Create by fangyanpeng 2017/08/14
 */
public class Log4J2LoggerListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    EagleLogger.useLog4J2Logger();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  }

}
