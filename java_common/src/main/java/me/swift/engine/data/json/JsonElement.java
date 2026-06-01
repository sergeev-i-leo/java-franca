package me.swift.engine.data.json;

import me.swift.engine.contract.TranspilableClass;

public class JsonElement extends TranspilableClass {

  @Override
  public void destroy() {
    super.destroy();
  }

  public String serialize() {
    return "";
  }

  public boolean isJsonBooleanPrimitive() {
    return false;
  }

  public boolean isJsonIntegerPrimitive() {
    return false;
  }

  public boolean isJsonDoublePrimitive() {
    return false;
  }

  public boolean isJsonStringPrimitive() {
    return false;
  }

  public boolean isJsonNull() {
    return false;
  }

  public boolean isJsonArray() {
    return false;
  }

  public boolean isJsonObject() {
    return false;
  }

  public JsonArray getAsJsonArray() {
    return null;
  }

  public JsonObject getAsJsonObject() {
    return null;
  }

  public String getAsString() {
    return "";
  }
}
