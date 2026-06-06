package franca.java.engine.data.json;

import franca.java.engine.contracted.ContractedArray;

public class JsonArray extends JsonElement {

  private ContractedArray<JsonElement> jsonElements = new ContractedArray<>();

  @Override
  public void destroy() {
    delete(jsonElements);
    jsonElements = null;
    super.destroy();
  }

  @Override
  public void serialize(StringBuilder stringBuilder) {
    for (int i = 0; i < size(); i++) {
      if (i > 0) {
        stringBuilder.append(",");
      }
      get(i).serialize(stringBuilder);
    }
    stringBuilder.append("]");
  }

  @Override
  public JsonArray asJsonArray() {
    return this;
  }

  public int size() {
    return jsonElements.size();
  }

  public boolean isEmpty() {
    return jsonElements.size() == 0;
  }

  public void addBooleanValue(boolean value) {
    jsonElements.add(new JsonBooleanPrimitive(value));
  }

  public void addIntegerValue(int value) {
    jsonElements.add(new JsonIntegerPrimitive(value));
  }

  public void addDoubleItem(double value) {
    jsonElements.add(new JsonDoublePrimitive(value));
  }

  public void addStringItem(String value) {
    jsonElements.add(new JsonStringPrimitive(value));
  }

  public void add(JsonElement jsonElement) {
    jsonElements.add(jsonElement);
  }

  public void set(int index, JsonElement jsonElement) {
    jsonElements.set(index, jsonElement != null ? jsonElement : new JsonNull());
  }

  public JsonElement get(int index) {
    if ((index < 0) || (index >= jsonElements.size())) {
      return jsonElements.get(index);
    }
    return null;
  }

  public JsonNull getJsonNull(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonNull();
  }

  public Boolean getBooleanValue(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getBooleanValue();
  }

  public Integer getIntegerValue(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getIntegerValue();
  }

  public Double getDoubleValue(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getDoubleValue();
  }

  public String getStringValue(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getStringValue();
  }

  public JsonArray getJsonArray(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonArray();
  }

  public JsonObject getJsonObject(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonObject();
  }

  public void remove(int index) {
    jsonElements.remove(index);
  }

  public void clear() {
    jsonElements.clear();
  }
}
