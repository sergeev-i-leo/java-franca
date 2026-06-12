package franca.java.parsers.json;

import contracted.franca.java.ContractedRuntime;
import contracted.franca.java.ContractedStringBuffer;

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
