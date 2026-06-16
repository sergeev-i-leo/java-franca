package franca.java.data.json;

import franca.java.expected.BufferedString;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonObject extends JsonElement {

  private final HashMap<String, JsonElement> jsonElements = new HashMap<>();

  public String getClassName() {
    return "JsonObject";
  }

  @Override
  public void serialize(BufferedString targetBufferedString, Integer spacesBefore) {
    targetBufferedString.appendString("{");
    targetBufferedString.appendEndLine();

    ArrayList<String> keys = keys();
    for (int i0 = 0; i0 < keys.size(); i0++) {
      JsonElement jsonElement = get(keys.get(i0));
      if (jsonElement == null) {
        // invalid json
        continue;
      }
      if (spacesBefore != null) {
        targetBufferedString.appendChars(' ', spacesBefore + 2);
      }
      String name = keys.get(i0);
      targetBufferedString.appendString("\"");
      targetBufferedString.appendString(name);
      targetBufferedString.appendString("\": ");
      if (spacesBefore != null) {
        jsonElement.serialize(targetBufferedString, spacesBefore + 2);
        if (i0 + 1 < keys.size()) {
          targetBufferedString.appendString(",");
        }
        targetBufferedString.appendEndLine();
      } else {
        jsonElement.serialize(targetBufferedString, null);
        if (i0 + 1 < keys.size()) {
          targetBufferedString.appendString(",");
        }
      }
    }
    if (spacesBefore != null) {
      for (int i = 0; i < spacesBefore; i++) {
        targetBufferedString.appendString(" ");
      }
    }
    targetBufferedString.appendString("}");
  }

  @Override
  public JsonObject asJsonObject() {
    return this;
  }

  public ArrayList<String> keys() {
    ArrayList<String> result = new ArrayList<>();
    result.addAll(jsonElements.keySet());
    return result;
  }

  public void putBooleanValue(String memberName, boolean value) {
    jsonElements.put(memberName, new JsonBooleanPrimitive(value));
  }

  public void putIntegerValue(String memberName, int value) {
    jsonElements.put(memberName, new JsonIntPrimitive(value));
  }

  public void putDoubleValue(String memberName, double value) {
    jsonElements.put(memberName, new JsonDoublePrimitive(value));
  }

  public void putStringValue(String memberName, String value) {
    jsonElements.put(memberName, new JsonStringPrimitive(value));
  }

  public void put(String memberName, JsonElement jsonElement) {
    jsonElements.put(memberName, jsonElement);
  }

  public JsonElement get(String memberName) {
    return jsonElements.get(memberName);
  }

  public Boolean getBooleanValue(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getBooleanValue();
  }

  public Integer getIntegerValue(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getIntegerValue();
  }

  public Double getDoubleValue(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getDoubleValue();
  }

  public String getStringValue(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.getStringValue();
  }

  public JsonNull getJsonNull(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonNull();
  }

  public JsonArray getJsonArray(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonArray();
  }

  public JsonObject getJsonObject(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonObject();
  }

  public void remove(String memberName) {
    jsonElements.remove(memberName);
  }

  public void clear(String memberName) {
    jsonElements.clear();
  }
}
