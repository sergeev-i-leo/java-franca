package franca.java.expected;

public class ExpectedRuntime {

  private static long nextId = 0L;

  public static synchronized long getId() {
    return ++nextId;
  }

  public static Integer stringToInteger(String input) {
    if (input == null) {
      return null;
    }
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  public static Integer hexStringToInteger(String input) {
    if (input == null) {
      return null;
    }
    if ((input.startsWith("0x")) || (input.startsWith("0X"))) {
      input = input.substring(2);
    } else if ((input.startsWith("x")) || (input.startsWith("X"))) {
      input = input.substring(1);
    }
    try {
      return Integer.parseInt(input, 16);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  public static Double stringToDouble(String input) {
    if (input == null) {
      return null;
    }
    try {
      return Double.parseDouble(input);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  public static String intToString(int value) {
    return Integer.toString(value);
  }

  public static String doubleToString(double value) {
    return Double.toString(value);
  }
}
