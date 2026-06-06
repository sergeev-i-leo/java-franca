package franca.swift.engine.data.json;

import franca.swift.engine.contract.SwiftRuntime;

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
  public String serialize() {
    return SwiftRuntime.intToString(value);
  }

  @Override
  public JsonIntegerPrimitive asJsonIntegerPrimitive() {
    return this;
  }

  @Override
  public Integer asInteger() {
    return value;
  }

  @Override
  public String asString() {
    return SwiftRuntime.intToString(value);
  }
}
