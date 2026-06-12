package franca.java.parsers.json;

import contracted.franca.java.ContractedArray;
import contracted.franca.java.ContractedDictionary;
import contracted.franca.java.ContractedStringBuffer;

public class JsonObject extends JsonElement {

  private final ContractedDictionary<String, JsonElement> jsonElements = new ContractedDictionary<>();

  @Override
  public void destroy() {
    delete(jsonElements);
    super.destroy();
  }

  @Override
  public void serialize(ContractedStringBuffer contractedStringBuffer, Integer spacesBefore) {
    contractedStringBuffer.appendString("{");
    contractedStringBuffer.endLine();

    ContractedArray<String> keys = keys();
    for (int i0 = 0; i0 < keys.size(); i0++) {
      JsonElement jsonElement = get(keys.get(i0));
      if (jsonElement == null) {
        // invalid json
        continue;
      }
      if (spacesBefore != null) {
        for (int i1 = 0; i1 < spacesBefore + 2; i1++) {
          contractedStringBuffer.appendString(" ");
        }
      }
      String name = keys.get(i0);
      contractedStringBuffer.appendString("\"");
      contractedStringBuffer.appendString(name);
      contractedStringBuffer.appendString("\": ");
      if (spacesBefore != null) {
        jsonElement.serialize(contractedStringBuffer, spacesBefore + 2 + name.length() + 4);
        if (i0 + 1 < keys.size()) {
          contractedStringBuffer.appendString(",");
        }
        contractedStringBuffer.endLine();
      } else {
        jsonElement.serialize(contractedStringBuffer, null);
        if (i0 + 1 < keys.size()) {
          contractedStringBuffer.appendString(",");
        }
      }
    }
    if (spacesBefore != null) {
      for (int i = 0; i < spacesBefore; i++) {
        contractedStringBuffer.appendString(" ");
      }
    }
    contractedStringBuffer.appendString("}");
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
