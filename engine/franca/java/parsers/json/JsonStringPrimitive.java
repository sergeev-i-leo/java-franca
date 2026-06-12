package franca.java.parsers.json;

import contracted.franca.java.ContractedStringBuffer;

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
  public void serialize(ContractedStringBuffer contractedStringBuffer, Integer spacesBefore) {
    String string = value;
    if (string == null) {
      string = "";
    }
    string = string.replace("\"", "\\\"");
    contractedStringBuffer.appendString("\"" + string + "\"");
  }

  @Override
  public String getStringValue() {
    return value;
  }
}
