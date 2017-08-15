package eagle.jfaster.org.logging;

/**
 * Create by fangyanpeng 2017/08/13
 */
public class ConsoleLoggerFactory extends InternalLoggerFactory {

  public static final InternalLoggerFactory INSTANCE = new ConsoleLoggerFactory();

  private ConsoleLoggerFactory() {
  }

  @Override
  protected InternalLogger newInstance(String name) {
    return new ConsoleLogger(name);
  }

}
