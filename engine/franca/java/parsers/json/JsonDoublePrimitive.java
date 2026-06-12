package franca.java.parsers.json;

import contracted.franca.java.ContractedRuntime;
import contracted.franca.java.ContractedStringBuffer;

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
  public void serialize(ContractedStringBuffer contractedStringBuffer, Integer spacesBefore) {
    contractedStringBuffer.appendString(ContractedRuntime.doubleToString(value));
  }

  @Override
  public Double getDoubleValue() {
    return value;
  }
}
