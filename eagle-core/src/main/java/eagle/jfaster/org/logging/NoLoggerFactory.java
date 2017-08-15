

package eagle.jfaster.org.logging;

/**
 * Create by fangyanpeng 2017/08/13
 */
public class NoLoggerFactory extends InternalLoggerFactory  {

  public static final InternalLoggerFactory INSTANCE = new NoLoggerFactory();

  private NoLoggerFactory() {
  }

  @Override
  protected InternalLogger newInstance(String name) {
    return new NoLogger(name);
  }

}
