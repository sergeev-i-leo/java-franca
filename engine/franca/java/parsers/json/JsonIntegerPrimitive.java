package franca.java.parsers.json;

import franca.java.expected.ExpectedRuntime;
import franca.java.expected.ExpectedStringBuilder;

public class JsonIntegerPrimitive extends JsonPrimitive {

  private final int value;

  public JsonIntegerPrimitive(int value) {
    this.value = value;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(ExpectedStringBuilder expectedStringBuilder, Integer spacesBefore) {
    expectedStringBuilder.appendString(ExpectedRuntime.intToString(value));
  }

  @Override
  public Integer getIntegerValue() {
    return value;
  }
}
