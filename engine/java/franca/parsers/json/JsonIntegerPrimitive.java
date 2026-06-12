package java.franca.parsers.json;

import contracted.java.franca.ContractedRuntime;
import contracted.java.franca.ContractedStringBuffer;

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
  public void serialize(ContractedStringBuffer contractedStringBuffer, Integer spacesBefore) {
    contractedStringBuffer.appendString(ContractedRuntime.intToString(value));
  }

  @Override
  public Integer getIntegerValue() {
    return value;
  }
}
