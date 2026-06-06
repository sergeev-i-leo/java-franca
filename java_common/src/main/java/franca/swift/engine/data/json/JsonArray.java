package franca.swift.engine.data.json;

import franca.swift.engine.contract.SwiftArray;

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
    if ((index < 0) || (index >= jsonElements.count())) {
      return jsonElements.get(index);
    }
    return null;
  }

  public JsonNull getJsonNullItem(int index) {
    JsonElement jsonElement = getItem(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonNull();
  }

  public Boolean getBooleanItem(int index) {
    JsonElement jsonElement = getItem(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asBoolean();
  }

  public Integer getIntegerItem(int index) {
    JsonElement jsonElement = getItem(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asInteger();
  }

  public Double getDoubleItem(int index) {
    JsonElement jsonElement = getItem(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asDouble();
  }

  public String getStringItem(int index) {
    JsonElement jsonElement = getItem(index);
    if (jsonElement == null) {
      return null;
    }
    JsonStringPrimitive jsonStringPrimitive = jsonElement.asJsonStringPrimitive();
    if (jsonStringPrimitive == null) {
      return null;
    }
    return jsonStringPrimitive.asString();
  }

  public JsonArray getJsonArrayItem(int index) {
    JsonElement jsonElement = getItem(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonArray();
  }

  public JsonObject getJsonObjectItem(int index) {
    JsonElement jsonElement = getItem(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonObject();
  }

  public void removeItem(int index) {
    jsonElements.removeAt(index);
  }
}
