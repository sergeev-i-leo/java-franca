package franca.java.data.json;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.BufferedString;

public class JsonDoublePrimitive extends JsonPrimitive {

  private final double value;

  public JsonDoublePrimitive(double value) {
    super();
    this.value = value;
  }

  @Override
  public void serialize(BufferedString targetBufferedString, Integer spacesBefore) {
    targetBufferedString.appendString(ExpectedRuntime.doubleToString(value));
  }

  @Override
  public Double getDoubleValue() {
    return value;
  }
}
