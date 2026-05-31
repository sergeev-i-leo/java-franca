package me.swift.engine.parsers.json;

public class JsonNull extends JsonElement {

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public String serialize() {
    return "null";
  }
}
