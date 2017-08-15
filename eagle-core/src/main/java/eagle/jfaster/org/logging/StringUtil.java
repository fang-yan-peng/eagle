package eagle.jfaster.org.logging;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * String utility class.
 */
public final class StringUtil {

  private StringUtil() {
    // Unused.
  }

  public static final String NEWLINE;

  static {
    String newLine;

    try {
      newLine = new Formatter().format("%n").toString();
    } catch (Exception e) {
      newLine = "\n";
    }

    NEWLINE = newLine;
  }

  private static final String EMPTY_STRING = "";

  /**
   * Splits the specified {@link String} with the specified delimiter.  This operation is a simplified and optimized
   * version of {@link String#split(String)}.
   */
  public static String[] split(String value, char delim) {
    final int end = value.length();
    final List<String> res = new ArrayList<String>();

    int start = 0;
    for (int i = 0; i < end; i++) {
      if (value.charAt(i) == delim) {
        if (start == i) {
          res.add(EMPTY_STRING);
        } else {
          res.add(value.substring(start, i));
        }
        start = i + 1;
      }
    }

    if (start == 0) { // If no delimiter was found in the value
      res.add(value);
    } else {
      if (start != end) {
        // Add the last element if it's not empty.
        res.add(value.substring(start, end));
      } else {
        // Truncate trailing empty elements.
        for (int i = res.size() - 1; i >= 0; i--) {
          if (res.get(i).isEmpty()) {
            res.remove(i);
          } else {
            break;
          }
        }
      }
    }

    return res.toArray(new String[res.size()]);
  }

  /**
   * The shortcut to {@link #simpleClassName(Class) simpleClassName(o.getClass())}.
   */
  public static String simpleClassName(Object o) {
    return simpleClassName(o.getClass());
  }

  /**
   * Generates a simplified name from a {@link Class}.  Similar to {@link Class#getSimpleName()}, but it works fine
   * with anonymous classes.
   */
  public static String simpleClassName(Class<?> clazz) {
    Package pkg = clazz.getPackage();
    if (pkg != null) {
      return clazz.getName().substring(pkg.getName().length() + 1);
    } else {
      return clazz.getName();
    }
  }
}
