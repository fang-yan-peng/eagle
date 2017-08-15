package eagle.jfaster.org.logging;

/**
 * Create by fangyanpeng 2017/08/13
 */
public class EagleLogger {

  public static void useSlf4JLogger() {
    InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
  }

  public static void useLog4J2Logger() {
    InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
  }

  public static void useLog4JLogger() {
    InternalLoggerFactory.setDefaultFactory(Log4JLoggerFactory.INSTANCE);
  }

  public static void useNoLogger() {
    InternalLoggerFactory.setDefaultFactory(NoLoggerFactory.INSTANCE);
  }

  public static void useConsoleLogger() {
    InternalLoggerFactory.setDefaultFactory(ConsoleLoggerFactory.INSTANCE);
  }

}
