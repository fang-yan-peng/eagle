package eagle.jfaster.org.logging;

import org.apache.logging.log4j.LogManager;

/**
 * Create by fangyanpeng 2017/08/14
 */
public class Log4J2LoggerFactory extends InternalLoggerFactory {

  public static final InternalLoggerFactory INSTANCE = new Log4J2LoggerFactory();

  private Log4J2LoggerFactory() {
  }

  @Override
  public InternalLogger newInstance(String name) {
    return new Log4J2Logger(LogManager.getLogger(name));
  }

}
