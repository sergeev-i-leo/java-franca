package franca.java.engine.data.json;

import franca.java.engine.contracted.ContractedRuntime;

public class JsonIntegerPrimitive extends JsonPrimitive {

  private int value;

  public JsonIntegerPrimitive(int value) {
    this.value = value;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(StringBuilder stringBuilder) {
    stringBuilder.append(ContractedRuntime.intToString(value));
  }

  @Override
  public Integer getIntegerValue() {
    return value;
  }
}
