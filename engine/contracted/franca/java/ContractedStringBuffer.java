package contracted.franca.java;

public class ContractedStringBuffer {

  private StringBuilder stringBuilder = new StringBuilder();

  public boolean isEmpty() {
    return stringBuilder.length() == 0;
  }

  public boolean isNotEmpty() {
    return stringBuilder.length() > 0;
  }

  public void appendCharacter(char character) {
    stringBuilder.append(character);
  }

  public void appendString(String string) {
    stringBuilder.append(string);
  }

  public void endLine() {
    appendString("\r\n");
  }

  public String getString() {
    return stringBuilder.toString();
  }

  public String getLowerCaseString() {
    return stringBuilder.toString().toLowerCase();
  }
}
