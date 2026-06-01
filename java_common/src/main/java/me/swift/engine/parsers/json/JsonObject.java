package me.swift.engine.parsers.json;

import me.swift.engine.contract.SwiftArray;
import me.swift.engine.contract.SwiftDictionary;

public class JsonObject extends JsonElement {

  private SwiftDictionary<String, JsonElement> jsonElements = new SwiftDictionary<>();

  @Override
  public void destroy() {
    delete(jsonElements);
    jsonElements = null;
    super.destroy();
  }

  @Override
  public String serialize() {
    StringBuilder text = new StringBuilder("{");
    setStringMember("$className", getClassName());
    SwiftArray<String> keys = keys();
    for (int i = 0; i < keys.count(); i++) {
      if (i > 0) {
        text.append(",");
      }
      text.append("\"").append(keys.get(i)).append("\":");
      JsonElement member = getMember(keys.get(i));
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

  public SwiftArray<String> keys() {
    return jsonElements.keys();
  }

  public void setBooleanMember(String memberName, boolean value) {
    jsonElements.put(memberName, new JsonBooleanPrimitive(value));
  }

  public void setIntegerMember(String memberName, int value) {
    jsonElements.put(memberName, new JsonIntegerPrimitive(value));
  }

  public void setDoubleMember(String memberName, double value) {
    jsonElements.put(memberName, new JsonDoublePrimitive(value));
  }

  public void setStringMember(String memberName, String value) {
    jsonElements.put(memberName, new JsonStringPrimitive(value));
  }

  public void setMember(String memberName, JsonElement jsonElement) {
    jsonElements.put(memberName, jsonElement);
  }

  public boolean isBooleanMember(String memberName) {
    JsonElement jsonElement = getMember(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isBoolean();
  }

  public boolean isIntegerMember(String memberName) {
    JsonElement jsonElement = getMember(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isInteger();
  }

  public boolean isDoubleMember(String memberName) {
    JsonElement jsonElement = getMember(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isDouble();
  }

  public boolean isStringMember(String memberName) {
    JsonElement jsonElement = getMember(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isString();
  }

  public boolean isJsonNullMember(String memberName) {
    JsonElement jsonElement = getMember(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isJsonNull();
  }

  public boolean isJsonArrayMember(String memberName) {
    JsonElement jsonElement = getMember(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isJsonArray();
  }

  public boolean isJsonObjectMember(String memberName) {
    JsonElement jsonElement = getMember(memberName);
    if (jsonElement == null) {
      return false;
    }
    return jsonElement.isJsonObject();
  }

  public boolean getBooleanMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isBoolean()) {
        return jsonPrimitive.getAsBoolean();
      }
    }
    return false;
  }

  public Integer getIntegerMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isInteger()) {
        return jsonPrimitive.getAsInteger();
      }
    }
    return null;
  }

  public Double getDoubleMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isDouble()) {
        return jsonPrimitive.getAsDouble();
      }
    }
    return null;
  }

  public String getStringMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement instanceof JsonBooleanPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonBooleanPrimitive) jsonElement;
      if (jsonPrimitive.isString()) {
        return jsonPrimitive.getAsString();
      }
    }
    return null;
  }

  public JsonElement getMember(String memberName) {
    return jsonElements.get(memberName);
  }

  public JsonNull getJsonNullMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonNull()) {
      return jsonElement.getAsJsonNull();
    }
    return null;
  }

  public JsonArray getJsonArrayMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonArray()) {
      return jsonElement.getAsJsonArray();
    }
    return null;
  }

  public JsonObject getJsonObjectMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonObject()) {
      return jsonElement.getAsJsonObject();
    }
    return null;
  }

  public void removeMember(String memberName) {
    jsonElements.remove(memberName);
  }
}
