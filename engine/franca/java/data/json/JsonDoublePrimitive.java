package franca.java.data.json;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.BufferedString;

public class JsonDoublePrimitive extends JsonPrimitive {

  private final double value;

  public JsonDoublePrimitive(double value) {
    super();
    this.value = value;
  }

  public String getClassName() {
    return "JsonDoublePrimitive";
  }

  @Override
  public void serialize(BufferedString bufferedString, Integer spacesBefore) {
    bufferedString.appendString(ExpectedRuntime.doubleToString(value));
  }

  @Override
  public Double getDoubleValue() {
    return value;
  }
}
