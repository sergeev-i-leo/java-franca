package franca.java.data.json;

import franca.java.expected.BufferedString;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonObject extends JsonElement {

  private final HashMap<String, JsonElement> members = new HashMap<>();

  @Override
  public JsonElement createCopy() {
    JsonObject resultJsonObject = new JsonObject();
    ArrayList<String> keys = this.keys();
    for (String key : keys) {
      JsonElement jsonElement = get(key);
      if (jsonElement != null) {
        jsonElement = jsonElement.createCopy();
        resultJsonObject.put(key, jsonElement);
      }
    }
    return resultJsonObject;
  }

  @Override
  public void serialize(BufferedString targetBufferedString, Integer spacesBefore) {
    targetBufferedString.appendString("{");
    targetBufferedString.finishLine();

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
        targetBufferedString.finishLine();
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

  public boolean isEmpty() {
    return members.size() == 0;
  }

  public boolean isNotEmpty() {
    return members.size() > 0;
  }

  public ArrayList<String> keys() {
    ArrayList<String> result = new ArrayList<>();
    result.addAll(members.keySet());
    return result;
  }

  public void putBooleanValue(String memberName, boolean value) {
    members.put(memberName, new JsonBooleanPrimitive(value));
  }

  public void putIntegerValue(String memberName, int value) {
    members.put(memberName, new JsonIntPrimitive(value));
  }

  public void putDoubleValue(String memberName, double value) {
    members.put(memberName, new JsonDoublePrimitive(value));
  }

  public void putStringValue(String memberName, String value) {
    members.put(memberName, new JsonStringPrimitive(value));
  }

  public void put(String memberName, JsonElement jsonElement) {
    members.put(memberName, jsonElement);
  }

  public JsonElement get(String memberName) {
    return members.get(memberName);
  }

  public Boolean getBooleanValue(String memberName) {
    JsonElement jsonElement = members.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asBooleanValue();
  }

  public Integer getIntegerValue(String memberName) {
    JsonElement jsonElement = members.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asIntegerValue();
  }

  public Double getDoubleValue(String memberName) {
    JsonElement jsonElement = members.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asDoubleValue();
  }

  public String getStringValue(String memberName) {
    JsonElement jsonElement = members.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asStringValue();
  }

  public JsonNull getJsonNull(String memberName) {
    JsonElement jsonElement = members.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonNull();
  }

  public JsonArray getJsonArray(String memberName) {
    JsonElement jsonElement = members.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonArray();
  }

  public JsonObject getJsonObject(String memberName) {
    JsonElement jsonElement = members.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonObject();
  }

  public void remove(String memberName) {
    members.remove(memberName);
  }

  public void clear(String memberName) {
    members.clear();
  }
}
