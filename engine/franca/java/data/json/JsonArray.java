package franca.java.data.json;

import franca.java.expected.BufferedString;

import java.util.ArrayList;

public class JsonArray extends JsonElement {

  private final ArrayList<JsonElement> jsonElements = new ArrayList<>();

  @Override
  public void serialize(BufferedString targetBufferedString, Integer spacesBefore) {
    targetBufferedString.appendString("[");
    targetBufferedString.appendEndLine();

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
        targetBufferedString.appendEndLine();
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
    return jsonElements.size();
  }

  public boolean isEmpty() {
    return jsonElements.size() == 0;
  }

  public boolean isNotEmpty() {
    return jsonElements.size() > 0;
  }

  public void addBooleanValue(boolean value) {
    jsonElements.add(new JsonBooleanPrimitive(value));
  }

  public void addIntegerValue(int value) {
    jsonElements.add(new JsonIntPrimitive(value));
  }

  public void addDoubleValue(double value) {
    jsonElements.add(new JsonDoublePrimitive(value));
  }

  public void addStringValue(String value) {
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
      return null;
    }
    return jsonElements.get(index);
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
