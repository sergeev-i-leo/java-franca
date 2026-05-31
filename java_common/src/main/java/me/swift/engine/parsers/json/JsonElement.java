package me.swift.engine.parsers.json;

import me.swift.engine.TranspilableClass;

public class JsonElement extends TranspilableClass {

  @Override
  public void destroy() {
    super.destroy();
  }

  public String serialize() {
    return "";
  }

  public boolean isBoolean() {
    return false;
  }

  public boolean isInteger() {
    return false;
  }

  public boolean isDouble() {
    return false;
  }

  public boolean isString() {
    return false;
  }

  public boolean isJsonNull() {
    return this instanceof JsonNull;
  }

  public boolean isJsonPrimitive() {
    return this instanceof JsonPrimitive;
  }

  public boolean isJsonObject() {
    return this instanceof JsonObject;
  }

  public boolean isJsonArray() {
    return this instanceof JsonArray;
  }

  public JsonNull getAsJsonNull() {
    if (isJsonNull()) {
      return (JsonNull) this;
    }
    return null;
  }

  public JsonPrimitive getAsJsonPrimitive() {
    if (isJsonPrimitive()) {
      return (JsonPrimitive) this;
    }
    return null;
  }

  public boolean getAsBoolean() {
    return false;
  }

  public int getAsInteger() {
    return 0;
  }

  public double getAsDouble() {
    return 0.0;
  }

  public String getAsString() {
    return "";
  }

  public JsonObject getAsJsonObject() {
    if (isJsonObject()) {
      return (JsonObject) this;
    }
    return null;
  }

  public JsonArray getAsJsonArray() {
    if (isJsonArray()) {
      return (JsonArray) this;
    }
    return null;
  }
}
