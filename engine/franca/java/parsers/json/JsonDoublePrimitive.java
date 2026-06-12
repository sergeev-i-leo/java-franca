package franca.java.parsers.json;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.ExpectedStringBuilder;

public class JsonDoublePrimitive extends JsonPrimitive {

  private final double value;

  public JsonDoublePrimitive(double value) {
    this.value = value;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(ExpectedStringBuilder expectedStringBuilder, Integer spacesBefore) {
    expectedStringBuilder.appendString(ExpectedRuntime.doubleToString(value));
  }

  @Override
  public Double getDoubleValue() {
    return value;
  }
}
