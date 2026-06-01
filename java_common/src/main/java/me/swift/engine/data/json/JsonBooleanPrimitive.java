package me.swift.engine.data.json;

public class JsonBooleanPrimitive extends JsonPrimitive {

  private boolean value;

  public JsonBooleanPrimitive(boolean value) {
    this.value = value;
  }

  @Override
  public String serialize() {
    return value ? "true" : "false";
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public boolean getAsBoolean() {
    return value;
  }

  @Override
  public String getAsString() {
    return value ? "true" : "false";
  }
}
