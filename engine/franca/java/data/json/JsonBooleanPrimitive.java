package franca.java.data.json;

import franca.java.expected.BufferedString;

public class JsonBooleanPrimitive extends JsonPrimitive {

  private final boolean value;

  public String getClassName() {
    return "JsonBooleanPrimitive";
  }

  public JsonBooleanPrimitive(boolean value) {
    super();
    this.value = value;
  }

  @Override
  public void serialize(BufferedString targetBufferedString, Integer spacesBefore) {
    targetBufferedString.appendString(value ? "true" : "false");
  }

  @Override
  public Boolean getBooleanValue() {
    return value;
  }
}
