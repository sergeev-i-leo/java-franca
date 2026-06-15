package franca.java.data.json;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.StringBuffer;

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
  public void serialize(StringBuffer stringBuffer, Integer spacesBefore) {
    stringBuffer.appendString(ExpectedRuntime.doubleToString(value));
  }

  @Override
  public Double getDoubleValue() {
    return value;
  }
}
