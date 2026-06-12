package franca.java.parsers.json;

import contracted.franca.java.ContractedStringBuffer;

public class JsonNull extends JsonElement {

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(ContractedStringBuffer contractedStringBuffer, Integer spacesBefore) {
    contractedStringBuffer.appendString("null");
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
