package me.swift.engine.parsers.json;

import me.swift.engine.expected.ExpectedRuntime;

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
    return ExpectedRuntime.intToString(value);
  }

  @Override
  public boolean isInteger() {
    return true;
  }

  @Override
  public int getAsInteger() {
    return value;
  }

  @Override
  public String getAsString() {
    return ExpectedRuntime.intToString(value);
  }
}
