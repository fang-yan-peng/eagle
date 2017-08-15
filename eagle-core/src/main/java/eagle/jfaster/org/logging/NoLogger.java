package eagle.jfaster.org.logging;

/**
 * Create by fangyanpeng 2017/08/13
 */
public class NoLogger extends AbstractInternalLogger {

  NoLogger(String name) {
    super(name);
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void trace(String msg) {
  }

  @Override
  public void trace(String format, Object arg) {
  }

  @Override
  public void trace(String format, Object argA, Object argB) {
  }

  @Override
  public void trace(String format, Object... arguments) {
  }

  @Override
  public void trace(String msg, Throwable t) {
  }

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public void debug(String msg) {
  }

  @Override
  public void debug(String format, Object arg) {
  }

  @Override
  public void debug(String format, Object argA, Object argB) {
  }

  @Override
  public void debug(String format, Object... arguments) {
  }

  @Override
  public void debug(String msg, Throwable t) {
  }

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void info(String msg) {
  }

  @Override
  public void info(String format, Object arg) {
  }

  @Override
  public void info(String format, Object argA, Object argB) {
  }

  @Override
  public void info(String format, Object... arguments) {
  }

  @Override
  public void info(String msg, Throwable t) {
  }

  @Override
  public boolean isWarnEnabled() {
    return false;
  }

  @Override
  public void warn(String msg) {
  }

  @Override
  public void warn(String format, Object arg) {
  }

  @Override
  public void warn(String format, Object... arguments) {
  }

  @Override
  public void warn(String format, Object argA, Object argB) {
  }

  @Override
  public void warn(String msg, Throwable t) {
  }

  @Override
  public boolean isErrorEnabled() {
    return false;
  }

  @Override
  public void error(String msg) {
  }

  @Override
  public void error(String format, Object arg) {
  }

  @Override
  public void error(String format, Object argA, Object argB) {
  }

  @Override
  public void error(String format, Object... arguments) {
  }

  @Override
  public void error(String msg, Throwable t) {
  }
}
