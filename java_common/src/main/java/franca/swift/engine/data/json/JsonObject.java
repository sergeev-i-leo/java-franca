package franca.swift.engine.data.json;

import franca.swift.engine.contract.SwiftArray;
import franca.swift.engine.contract.SwiftDictionary;

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
  public JsonObject asJsonObject() {
    return this;
  }

  public SwiftArray<String> keys() {
    return jsonElements.keys();
  }

  public void setMember(String memberName, JsonElement jsonElement) {
    jsonElements.put(memberName, jsonElement);
  }

  public void setJsonNullMember(String memberName) {
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
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonNull();
  }

  public Boolean getBooleanMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asBoolean();
  }

  public Integer getIntegerMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asInteger();
  }

  public Double getDoubleMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asDouble();
  }

  public String getStringMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    JsonStringPrimitive jsonStringPrimitive = jsonElement.asJsonStringPrimitive();
    if (jsonStringPrimitive == null) {
      return null;
    }
    return jsonStringPrimitive.asString();
  }

  public JsonArray getJsonArrayMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonArray();
  }

  public JsonObject getJsonObjectMember(String memberName) {
    JsonElement jsonElement = jsonElements.get(memberName);
    if (jsonElement == null) {
      return null;
    }
    return jsonElement.asJsonObject();
  }

  public void removeMember(String memberName) {
    jsonElements.remove(memberName);
  }
}
