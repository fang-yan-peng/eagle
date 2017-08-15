package eagle.jfaster.org.logging;

import org.apache.logging.log4j.Logger;

/**
 * Create by fangyanpeng 2017/08/13
 */
public class Log4J2Logger extends AbstractInternalLogger {

  private static final long serialVersionUID = 5485418394879791397L;

  private final transient Logger logger;

  Log4J2Logger(Logger logger) {
    super(logger.getName());
    this.logger = logger;
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void trace(String msg) {
    logger.trace(msg);
  }

  @Override
  public void trace(String format, Object arg) {
    logger.trace(format, arg);
  }

  @Override
  public void trace(String format, Object argA, Object argB) {
    logger.trace(format, argA, argB);
  }

  @Override
  public void trace(String format, Object... arguments) {
    logger.trace(format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    logger.trace(msg, t);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    logger.debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    logger.debug(format, arg);
  }

  @Override
  public void debug(String format, Object argA, Object argB) {
    logger.debug(format, argA, argB);
  }

  @Override
  public void debug(String format, Object... arguments) {
    logger.debug(format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    logger.debug(msg, t);
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    logger.info(msg);
  }

  @Override
  public void info(String format, Object arg) {
    logger.info(format, arg);
  }

  @Override
  public void info(String format, Object argA, Object argB) {
    logger.info(format, argA, argB);
  }

  @Override
  public void info(String format, Object... arguments) {
    logger.info(format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    logger.info(msg, t);
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    logger.warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    logger.warn(format, arg);
  }

  @Override
  public void warn(String format, Object... arguments) {
    logger.warn(format, arguments);
  }

  @Override
  public void warn(String format, Object argA, Object argB) {
    logger.warn(format, argA, argB);
  }

  @Override
  public void warn(String msg, Throwable t) {
    logger.warn(msg, t);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    logger.error(msg);
  }

  @Override
  public void error(String format, Object arg) {
    logger.error(format, arg);
  }

  @Override
  public void error(String format, Object argA, Object argB) {
    logger.error(format, argA, argB);
  }

  @Override
  public void error(String format, Object... arguments) {
    logger.error(format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    logger.error(msg, t);
  }

}
