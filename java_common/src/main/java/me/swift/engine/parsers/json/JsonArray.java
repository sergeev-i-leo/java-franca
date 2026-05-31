package me.swift.engine.parsers.json;

import me.swift.engine.expected.ExpectedList;

public class JsonArray extends JsonElement {

  private ExpectedList<JsonElement> jsonElements = new ExpectedList<>();

  @Override
  public void destroy() {
    jsonElements.destroyAll();
    jsonElements.destroy();
    jsonElements = null;
    super.destroy();
  }

  @Override
  public String serialize() {
    StringBuilder text = new StringBuilder("[");
    for (int i = 0; i < size(); i++) {
      if (i > 0) {
        text.append(",");
      }
      text.append(get(i).serialize());
    }
    text.append("]");
    return text.toString();
  }

  public int size() {
    return jsonElements.size();
  }

  public boolean isEmpty() {
    return jsonElements.size() == 0;
  }

  public void addBoolean(boolean value) {
    jsonElements.add(new JsonBooleanPrimitive(value));
  }

  public void addInteger(int value) {
    jsonElements.add(new JsonIntegerPrimitive(value));
  }

  public void addDouble(double value) {
    jsonElements.add(new JsonDoublePrimitive(value));
  }

  public void addString(String value) {
    jsonElements.add(new JsonStringPrimitive(value));
  }

  public void add(JsonElement jsonElement) {
    jsonElements.add(jsonElement);
  }

  public void set(int index, JsonElement jsonElement) {
    jsonElements.set(index, jsonElement != null ? jsonElement : new JsonNull());
  }

  public JsonElement get(int index) {
    return jsonElements.get(index);
  }

  public JsonElement remove(int index) {
    return jsonElements.removeAt(index);
  }
}
