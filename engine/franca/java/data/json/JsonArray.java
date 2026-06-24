package franca.java.data.json;

import franca.java.expected.BufferedString;

import java.util.ArrayList;

public class JsonArray extends JsonElement {

  private final ArrayList<JsonElement> elements = new ArrayList<>();

  @Override
  public JsonElement createCopy() {
    JsonArray resultJsonArray = new JsonArray();
    for (int i = 0; i < size(); i++) {
      JsonElement jsonElement = get(i);
      if (jsonElement != null) {
        jsonElement = jsonElement.createCopy();
        resultJsonArray.add(jsonElement);
      }
    }
    return resultJsonArray;
  }

  @Override
  public void serialize(BufferedString targetBufferedString, Integer spacesBefore) {
    targetBufferedString.appendString("[");
    targetBufferedString.finishLine();

    for (int i0 = 0; i0 < size(); i0++) {
      JsonElement jsonElement = get(i0);
      if (jsonElement == null) {
        // invalid json
        continue;
      }
      if (spacesBefore != null) {
        targetBufferedString.appendChars(' ', spacesBefore + 2);
        jsonElement.serialize(targetBufferedString, spacesBefore + 2);
        if (i0 + 1 < size()) {
          targetBufferedString.appendString(",");
        }
        targetBufferedString.finishLine();
      } else {
        jsonElement.serialize(targetBufferedString, null);
        if (i0 + 1 < size()) {
          targetBufferedString.appendString(",");
        }
      }
    }
    if (spacesBefore != null) {
      for (int i1 = 0; i1 < spacesBefore; i1++) {
        targetBufferedString.appendString(" ");
      }
    }
    targetBufferedString.appendString("]");
  }

  @Override
  public JsonArray asJsonArray() {
    return this;
  }

  public int size() {
    return elements.size();
  }

  public boolean isEmpty() {
    return elements.size() == 0;
  }

  public boolean isNotEmpty() {
    return elements.size() > 0;
  }

  public void addBooleanValue(boolean value) {
    elements.add(new JsonBooleanPrimitive(value));
  }

  public void addIntegerValue(int value) {
    elements.add(new JsonIntPrimitive(value));
  }

  public void addDoubleValue(double value) {
    elements.add(new JsonDoublePrimitive(value));
  }

  public void addStringValue(String value) {
    elements.add(new JsonStringPrimitive(value));
  }

  public void add(JsonElement jsonElement) {
    elements.add(jsonElement);
  }

  public void set(int index, JsonElement jsonElement) {
    elements.set(index, jsonElement != null ? jsonElement : new JsonNull());
  }

  public JsonElement get(int index) {
    if ((index < 0) || (index >= elements.size())) {
      return null;
    }
    return elements.get(index);
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
    return jsonElement.asBooleanValue();
  }

  public Integer getIntegerValue(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asIntegerValue();
  }

  public Double getDoubleValue(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asDoubleValue();
  }

  public String getStringValue(int index) {
    JsonElement jsonElement = get(index);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asStringValue();
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
    elements.remove(index);
  }

  public void clear() {
    elements.clear();
  }
}
