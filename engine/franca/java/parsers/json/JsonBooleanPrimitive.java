package franca.java.parsers.json;

import franca.java.expected.ExpectedStringBuilder;

public class JsonBooleanPrimitive extends JsonPrimitive {

  private final boolean value;

  public JsonBooleanPrimitive(boolean value) {
    this.value = value;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(ExpectedStringBuilder expectedStringBuilder, Integer spacesBefore) {
    expectedStringBuilder.appendString(value ? "true" : "false");
  }

  @Override
  public Boolean getBooleanValue() {
    return value;
  }
}
