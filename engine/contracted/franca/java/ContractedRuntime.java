package contracted.franca.java;

public class ContractedRuntime {

  public static Integer parseInt(String input) {
    if (input == null) {
      return null;
    }
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  public static Integer parseHexInt(String input) {
    if (input == null) {
      return null;
    }
    if ((input.startsWith("x")) || (input.startsWith("X"))) {
      input = input.substring(1);
    }
    try {
      return Integer.parseInt(input, 16);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  public static Double parseDouble(String input) {
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
