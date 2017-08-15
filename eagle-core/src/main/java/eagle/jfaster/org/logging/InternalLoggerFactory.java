package eagle.jfaster.org.logging;


public abstract class InternalLoggerFactory {
  private static volatile InternalLoggerFactory defaultFactory;

  static {
    final String name = InternalLoggerFactory.class.getName();
    InternalLoggerFactory f;
    try {
      f = new Slf4JLoggerFactory(true);
      f.newInstance(name).debug("Using SLF4J as the default logging framework");
    } catch (Throwable t1) {
      try {
        f = Log4J2LoggerFactory.INSTANCE;
        f.newInstance(name).debug("Using Log4J2 as the default logging framework");
      } catch (Throwable t2) {
        try {
          f = Log4JLoggerFactory.INSTANCE;
          f.newInstance(name).debug("Using Log4J as the default logging framework");
        } catch (Throwable t3) {
          f = NoLoggerFactory.INSTANCE;
        }
      }
    }
    defaultFactory = f;
  }

  /**
   * Returns the default factory.  The initial default factory is
   */
  public static InternalLoggerFactory getDefaultFactory() {
    return defaultFactory;
  }

  /**
   * Changes the default factory.
   */
  public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
    if (defaultFactory == null) {
      throw new NullPointerException("defaultFactory");
    }
    InternalLoggerFactory.defaultFactory = defaultFactory;
  }

  /**
   * Creates a new logger instance with the name of the specified class.
   */
  public static InternalLogger getInstance(Class<?> clazz) {
    return getInstance(clazz.getName());
  }

  /**
   * Creates a new logger instance with the specified name.
   */
  public static InternalLogger getInstance(String name) {
    return getDefaultFactory().newInstance(name);
  }

  /**
   * Creates a new logger instance with the specified name.
   */
  protected abstract InternalLogger newInstance(String name);
}
