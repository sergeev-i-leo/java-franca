package franca.swift.engine.data.json;

import franca.swift.engine.contract.SwiftRuntime;

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
  public String serialize() {
    return SwiftRuntime.doubleToString(value);
  }

  @Override
  public JsonDoublePrimitive asJsonDoublePrimitive() {
    return this;
  }

  @Override
  public Double asDouble() {
    return value;
  }

  @Override
  public String asString() {
    return SwiftRuntime.doubleToString(value);
  }
}
