package franca.java.parsers.json;

import franca.java.parsers.Parser;

import java.util.ArrayList;

public class JsonParser extends Parser {

  @Override
  public void destroy() {
    super.destroy();
  }

  public JsonElement parse(String input) {
    if (input == null) {
      return new JsonObject();
    }

    if (this.input != null) {
      delete(this.input);
    }
    this.input = copyOf(input);

    position = 0;
    JsonElement jsonElement = parseJsonElement();
    if (jsonElement != null) {
      return jsonElement;
    }
    return new JsonElement();
  }

  private JsonElement parseJsonElement() {
    skipWhitespaces();
    if (input.startsWith("null", position)) {
      position += 4;
      return new JsonNull();
    }
    JsonElement jsonElement = parseJsonObject();
    if (jsonElement != null) {
      return jsonElement;
    }
    jsonElement = parseJsonArray();
    if (jsonElement != null) {
      return jsonElement;
    }
    String literalType = parseLiteral();
    if (literalType.equals("boolean-literal")) {
      return new JsonBooleanPrimitive(booleanLiteral);
    }
    if (literalType.equals("integer-literal")) {
      return new JsonIntPrimitive(integerLiteral);
    }
    if (literalType.equals("hex-integer-literal")) {
      return new JsonIntPrimitive(integerLiteral);
    }
    if (literalType.equals("double-literal")) {
      return new JsonDoublePrimitive(doubleLiteral);
    }
    if (literalType.equals("string-literal")) {
      JsonStringPrimitive jsonStringPrimitive = new JsonStringPrimitive(literalStringBuffer.getString());
      return jsonStringPrimitive;
    }
    System.out.println("Invalid JsonElement at " + position);
    return null;
  }

  private JsonObject parseJsonObject() {
    if (peekChar() != '{') {
      return null;
    }
    skipChars(1);
    JsonObject jsonObject = new JsonObject();
    skipWhitespaces();
    if (peekChar() == '}') {
      skipChars(1);
      return jsonObject;
    }
    while (true) {
      parseJsonObjectMember(jsonObject);
      skipWhitespaces();
      if (peekChar() != ',') {
        break;
      }
      skipChars(1);
    }
    if (peekChar() != '}') {
      System.out.println("Missing '}' at " + position);
      skipChars(1);
      return null;
    }
    skipChars(1);
    return recreateJsonObjectByClassName(jsonObject);
  }

  protected JsonObject recreateJsonObjectByClassName(JsonObject jsonObject) {
    // for creating specific classes by $className
    return jsonObject;
  }

  private void cloneJsonObject(JsonObject sourceJsonObject, JsonObject targetJsonObject) {
    ArrayList<String> keys = sourceJsonObject.keys();
    for (int i = 0; i < keys.size(); i++) {
      String key = keys.get(i);
      targetJsonObject.put(key, sourceJsonObject.get(key));
    }
  }

  private void parseJsonObjectMember(JsonObject jsonObject) {
    try {
      skipWhitespaces();
      String literalType = parseStringLiteral();
      if (!literalType.equals("string")) {
        // error
        skipChars(1);
        System.out.println("JsonObject name expected at " + position);
        return;
      }
      skipWhitespaces();
      if (peekChar() == ':') {
        skipChars(1);
        JsonElement jsonElement = parseJsonElement();
        jsonObject.put(literalStringBuffer.getString(), jsonElement);
      }
    } catch (Exception e) {
      System.out.println("End of input at " + position);
    }
  }

  private JsonArray parseJsonArray() {
    if (peekChar() != '[') {
      return null;
    }
    skipChars(1);
    JsonArray jsonArray = new JsonArray();
    while (position < input.length()) {
      skipWhitespaces();
      if (peekChar() == ']') {
        break;
      }
      JsonElement jsonElement = parseJsonElement();
      jsonArray.add(jsonElement);
      if (peekChar() != ',') {
        break;
      }
      skipChars(1);
    }
    if (peekChar() != ']') {
      System.out.println("Missing ']' at " + position);
      skipChars(1);
      return null;
    }
    skipChars(1);
    return jsonArray;
  }
}
