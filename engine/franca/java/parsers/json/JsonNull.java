package franca.java.parsers.json;

import franca.java.expected.ExpectedStringBuilder;

public class JsonNull extends JsonElement {

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(ExpectedStringBuilder expectedStringBuilder, Integer spacesBefore) {
    expectedStringBuilder.appendString("null");
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
