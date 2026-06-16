package franca.java.data.json;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.BufferedString;

public class JsonIntPrimitive extends JsonPrimitive {

  private final int value;

  public JsonIntPrimitive(int value) {
    super();
    this.value = value;
  }

  public String getClassName() {
    return "JsonIntPrimitive";
  }

  @Override
  public void serialize(BufferedString targetBufferedString, Integer spacesBefore) {
    targetBufferedString.appendString(ExpectedRuntime.intToString(value));
  }

  @Override
  public Integer getIntegerValue() {
    return value;
  }
}
