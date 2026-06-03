package me.swift.engine.data.json;

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

  @Override
  public boolean isJsonObject() {
    return true;
  }

  @Override
  public JsonObject asJsonObject() {
    return this;
  }

  public SwiftArray<String> keys() {
    return jsonElements.keys();
  }

  public void setMember(String memberName, JsonElement jsonElement) {
    jsonElements.put(memberName, jsonElement);
  }

  public void setNullMember(String memberName) {
    jsonElements.put(memberName, new JsonNull());
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

  public JsonElement getMember(String memberName) {
    return jsonElements.get(memberName);
  }

  public JsonNull getJsonNullMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonNull()) {
      return (JsonNull) jsonElement;
    }
    return null;
  }

  public Boolean getBooleanMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonBooleanPrimitive()) {
      return ((JsonBooleanPrimitive) jsonElement).getValue();
    }
    return null;
  }

  public Integer getIntegerMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonIntegerPrimitive()) {
      return ((JsonIntegerPrimitive) jsonElement).getValue();
    }
    return null;
  }

  public Double getDoubleMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement.isJsonDoublePrimitive()) {
      return ((JsonDoublePrimitive) jsonElement).getValue();
    }
    return null;
  }

  public String getStringMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    if (jsonElement.isJsonStringPrimitive()) {
      return ((JsonStringPrimitive) jsonElement).getValue();
    }
    return null;
  }

  public JsonArray getJsonArrayMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    if (jsonElement.isJsonArray()) {
      return (JsonArray) jsonElement;
    }
    return null;
  }

  public JsonObject getJsonObjectMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    if (jsonElement.isJsonObject()) {
      return (JsonObject) jsonElement;
    }
    return null;
  }

  public void removeMember(String memberName) {
    jsonElements.remove(memberName);
  }
}
