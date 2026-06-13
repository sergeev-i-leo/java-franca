package franca.java.expected;

public class StringBuffer {

  private StringBuilder stringBuilder = new StringBuilder();

  public boolean isEmpty() {
    return stringBuilder.length() == 0;
  }

  public boolean isNotEmpty() {
    return stringBuilder.length() > 0;
  }

  public void appendChar(char c) {
    stringBuilder.append(c);
  }

  public void appendString(String string) {
    stringBuilder.append(string);
  }

  public void appendSpaces(int number) {
    while (number > 0) {
      appendChar(' ');
      number--;
    }
  }

  public void appendEndLine() {
    appendString("\r\n");
  }

  public String getString() {
    return stringBuilder.toString();
  }

  public String getLowerCaseString() {
    return stringBuilder.toString().toLowerCase();
  }
}
