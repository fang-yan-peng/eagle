package eagle.jfaster.org.logging.plugin;

import eagle.jfaster.org.logging.EagleLogger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Create by fangyanpeng 2017/08/14
 */
public class Log4JLoggerListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    EagleLogger.useLog4JLogger();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  }

}
