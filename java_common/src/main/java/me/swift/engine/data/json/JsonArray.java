package me.swift.engine.data.json;

import me.swift.engine.contract.SwiftArray;

public class JsonArray extends JsonElement {

  private SwiftArray<JsonElement> jsonElements = new SwiftArray<>();

  @Override
  public void destroy() {
    delete(jsonElements);
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
      text.append(getElement(i).serialize());
    }
    text.append("]");
    return text.toString();
  }

  @Override
  public boolean isJsonArray() {
    return true;
  }

  @Override
  public JsonArray getAsJsonArray() {
    return this;
  }

  public int size() {
    return jsonElements.count();
  }

  public boolean isEmpty() {
    return jsonElements.count() == 0;
  }

  public void addBooleanElement(boolean value) {
    jsonElements.append(new JsonBooleanPrimitive(value));
  }

  public void addIntegerElement(int value) {
    jsonElements.append(new JsonIntegerPrimitive(value));
  }

  public void addDoubleElement(double value) {
    jsonElements.append(new JsonDoublePrimitive(value));
  }

  public void addStringElement(String value) {
    jsonElements.append(new JsonStringPrimitive(value));
  }

  public void addElement(JsonElement jsonElement) {
    jsonElements.append(jsonElement);
  }

  public void setElement(int index, JsonElement jsonElement) {
    jsonElements.set(index, jsonElement != null ? jsonElement : new JsonNull());
  }

  public JsonElement getElement(int index) {
    return jsonElements.get(index);
  }

  public void removeElement(int index) {
    jsonElements.removeAt(index);
  }
}
