package franca.java.transpiler.data.json;

import franca.java.transpiler.contracted.ContractedRuntime;

public class JsonDoublePrimitive extends JsonPrimitive {

  private double value;

  public JsonDoublePrimitive(double value) {
    this.value = value;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public void serialize(StringBuilder stringBuilder) {
    stringBuilder.append(ContractedRuntime.doubleToString(value));
  }

  @Override
  public Double getDoubleValue() {
    return value;
  }
}
