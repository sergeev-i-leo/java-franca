package franca.java.transpiler.data.json;

import franca.java.transpiler.contracted.ContractedArray;
import franca.java.transpiler.contracted.ContractedDictionary;

public class JsonObject extends JsonElement {

  private ContractedDictionary<String, JsonElement> jsonElements = new ContractedDictionary<>();

  @Override
  public void destroy() {
    delete(jsonElements);
    jsonElements = null;
    super.destroy();
  }

  @Override
  public void serialize(StringBuilder stringBuilder) {
    putStringValue("$className", getClassName());
    ContractedArray<String> keys = keys();
    for (int i = 0; i < keys.size(); i++) {
      if (i > 0) {
        stringBuilder.append(",");
      }
      stringBuilder.append("\"").append(keys.get(i)).append("\":");
      JsonElement member = get(keys.get(i));
      if (member != null) {
        member.serialize(stringBuilder);
      }
    }
    stringBuilder.append("}");
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

  public ContractedArray<String> keys() {
    return jsonElements.keys();
  }

  public void putBooleanValue(String memberName, boolean value) {
    jsonElements.put(memberName, new JsonBooleanPrimitive(value));
  }

  public void putIntegerValue(String memberName, int value) {
    jsonElements.put(memberName, new JsonIntegerPrimitive(value));
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
