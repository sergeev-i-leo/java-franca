package franca.java.parsers.json;

import franca.java.expected.ExpectedStringBuilder;

public class JsonStringPrimitive extends JsonPrimitive {

  private String value;

  public JsonStringPrimitive(String value) {
    this.value = copyOf(value);
  }

  @Override
  public void destroy() {
    delete(value);
    super.destroy();
  }

  @Override
  public void serialize(ExpectedStringBuilder expectedStringBuilder, Integer spacesBefore) {
    String string = value;
    if (string == null) {
      string = "";
    }
    string = string.replace("\"", "\\\"");
    expectedStringBuilder.appendString("\"" + string + "\"");
  }

  @Override
  public String getStringValue() {
    return value;
  }
}
