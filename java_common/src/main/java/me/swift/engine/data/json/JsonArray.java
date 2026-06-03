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
    for (int i = 0; i < count(); i++) {
      if (i > 0) {
        text.append(",");
      }
      text.append(getItem(i).serialize());
    }
    text.append("]");
    return text.toString();
  }

  @Override
  public boolean isJsonArray() {
    return true;
  }

  @Override
  public JsonArray asJsonArray() {
    return this;
  }

  public int count() {
    return jsonElements.count();
  }

  public boolean isEmpty() {
    return jsonElements.count() == 0;
  }

  public void appendItem(JsonElement jsonElement) {
    jsonElements.append(jsonElement);
  }

  public void appendJsonNullItem() {
    jsonElements.append(new JsonNull());
  }

  public void appendBooleanItem(boolean value) {
    jsonElements.append(new JsonBooleanPrimitive(value));
  }

  public void appendIntegerItem(int value) {
    jsonElements.append(new JsonIntegerPrimitive(value));
  }

  public void appendDoubleItem(double value) {
    jsonElements.append(new JsonDoublePrimitive(value));
  }

  public void appendStringItem(String value) {
    jsonElements.append(new JsonStringPrimitive(value));
  }

  public void setItem(int index, JsonElement jsonElement) {
    jsonElements.set(index, jsonElement != null ? jsonElement : new JsonNull());
  }

  public JsonElement getItem(int index) {
    return jsonElements.get(index);
  }

  public void removeItem(int index) {
    jsonElements.removeAt(index);
  }
}
