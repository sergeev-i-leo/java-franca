package me.swift.engine.parsers.json;

import me.swift.engine.expected.ExpectedList;
import me.swift.engine.expected.ExpectedMap;

public class JsonObject extends JsonElement {

  private ExpectedMap<JsonElement> jsonElements = new ExpectedMap<>();

  @Override
  public void destroy() {
    jsonElements.destroyAll();
    jsonElements.destroy();
    super.destroy();
  }

  @Override
  public String serialize() {
    StringBuilder text = new StringBuilder("{");
    setString("$className", getClassName());
    ExpectedList<String> keys = keysExpectedList();
    for (int i = 0; i < keys.size(); i++) {
      if (i > 0) {
        text.append(",");
      }
      text.append("\"").append(keys.get(i)).append("\":");
      JsonElement member = get(keys.get(i));
      if (member != null) {
        text.append(member.serialize());
      }
    }
    text.append("}");
    return text.toString();
  }

  public String getClassName() {
    return "JsonObject";
  }

  public void deserialize(JsonObject parsedJsonObject) {
  }

  public ExpectedList<String> keysExpectedList() {
    return jsonElements.keys();
  }

  public void setBoolean(String memberName, boolean value) {
    jsonElements.set(memberName, new JsonBooleanPrimitive(value));
  }

  public void setInt(String memberName, int value) {
    jsonElements.set(memberName, new JsonIntegerPrimitive(value));
  }

  public void setDouble(String memberName, double value) {
    jsonElements.set(memberName, new JsonDoublePrimitive(value));
  }

  public void setString(String memberName, String value) {
    jsonElements.set(memberName, new JsonStringPrimitive(value));
  }

  public void set(String memberName, JsonElement jsonElement) {
    jsonElements.set(memberName, jsonElement);
  }

  public boolean isBoolean(String memberName) {
    JsonElement jsonElement = get(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isBoolean();
  }

  public boolean isInteger(String memberName) {
    JsonElement jsonElement = get(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isInteger();
  }

  public boolean isDouble(String memberName) {
    JsonElement jsonElement = get(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isDouble();
  }

  public boolean isString(String memberName) {
    JsonElement jsonElement = get(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isString();
  }

  public boolean isJsonNull(String memberName) {
    JsonElement jsonElement = get(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isJsonNull();
  }

  public boolean isJsonArray(String memberName) {
    JsonElement jsonElement = get(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isJsonArray();
  }

  public boolean isJsonObject(String memberName) {
    JsonElement jsonElement = get(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isJsonObject();
  }

  public boolean getAsBoolean(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isBoolean()) {
        return jsonPrimitive.getAsBoolean();
      }
    }
    return false;
  }

  public Integer getAsInteger(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isInteger()) {
        return jsonPrimitive.getAsInteger();
      }
    }
    return null;
  }

  public Double getAsDouble(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isDouble()) {
        return jsonPrimitive.getAsDouble();
      }
    }
    return null;
  }

  public String getAsString(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isString()) {
        return jsonPrimitive.getAsString();
      }
    }
    return null;
  }

  public JsonElement get(String memberName) {
    return jsonElements.get(memberName);
  }

  public JsonNull getAsJsonNull(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonNull()) {
      return jsonElement.getAsJsonNull();
    }
    return null;
  }

  public JsonArray getAsJsonArray(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonArray()) {
      return jsonElement.getAsJsonArray();
    }
    return null;
  }

  public JsonObject getAsJsonObject(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonObject()) {
      return jsonElement.getAsJsonObject();
    }
    return null;
  }

  public void remove(String memberName) {
    jsonElements.remove(memberName);
  }
}
