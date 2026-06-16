package franca.java.expected;

public class BufferedString {

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

  public void appendChars(char c, int number) {
    while (number > 0) {
      appendChar(c);
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

  public void clear() {
    stringBuilder = new StringBuilder();
  }
}
