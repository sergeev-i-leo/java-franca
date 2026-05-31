package me.swift.engine.parsers.json;

import me.swift.engine.expected.ExpectedRuntime;

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
    return ExpectedRuntime.doubleToString(value);
  }

  @Override
  public boolean isDouble() {
    return true;
  }

  @Override
  public double getAsDouble() {
    return value;
  }

  @Override
  public String getAsString() {
    return ExpectedRuntime.doubleToString(value);
  }
}
