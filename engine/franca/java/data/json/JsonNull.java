package franca.java.data.json;

import franca.java.expected.BufferedString;

public class JsonNull extends JsonElement {

  public String getClassName() {
    return "JsonNull";
  }

  @Override
  public void serialize(BufferedString bufferedString, Integer spacesBefore) {
    bufferedString.appendString("null");
  }

  @Override
  public JsonNull asJsonNull() {
    return this;
  }

  @Override
  public String getStringValue() {
    return "null";
  }
}
