package eagle.jfaster.org.logging.plugin;

import eagle.jfaster.org.logging.EagleLogger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Create by fangyanpeng 2017/08/14
 */
public class ConsoleLoggerListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    EagleLogger.useConsoleLogger();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  }

}
