package franca.java.data.json;

import franca.java.expected.BufferedString;

public class JsonStringPrimitive extends JsonPrimitive {

  private String value;

  public JsonStringPrimitive(String value) {
    super();
    this.value = value;
  }

  public String getClassName() {
    return "JsonStringPrimitive";
  }

  @Override
  public void serialize(BufferedString bufferedString, Integer spacesBefore) {
    String string = value;
    if (string == null) {
      string = "";
    }
    string = string.replace("\"", "\\\"");
    bufferedString.appendString("\"" + string + "\"");
  }

  @Override
  public String getStringValue() {
    return value;
  }
}
