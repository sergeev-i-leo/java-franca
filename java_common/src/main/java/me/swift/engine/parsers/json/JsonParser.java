package me.swift.engine.parsers.json;

import me.swift.engine.contract.TranspilableClass;
import me.swift.engine.contract.*;

public class JsonParser extends TranspilableClass {

  private String input = null;
  private int position = 0;

  public JsonParser() {
  }

  @Override
  public void destroy() {
    if (input != null) {
      delete(input);
      input = null;
    }
    super.destroy();
  }

  public JsonElement parse(String input) {
    if (input == null) {
      return new JsonStringPrimitive("404");
    }
    if (this.input != null) {
      delete(this.input);
    }
    this.input = input;
    position = 0;
    JsonElement jsonElement = parseJsonElement();
    if (jsonElement != null) {
      return jsonElement;
    }
    return new JsonElement();
  }

  private JsonElement parseJsonElement() {
    skipWhitespaces();
    JsonElement jsonElement = parseJsonValue();
    skipWhitespaces();
    return jsonElement;
  }

  private JsonElement parseJsonValue() {
    JsonElement jsonElement = parseJsonObject();
    if (jsonElement != null) {
      return jsonElement;
    }
    jsonElement = parseJsonArray();
    if (jsonElement != null) {
      return jsonElement;
    }
    String string = parseString();
    if (string != null) {
      return new JsonStringPrimitive(string);
    }
    JsonPrimitive numberJsonPrimitive = parseNumber();
    if (numberJsonPrimitive != null) {
      return numberJsonPrimitive;
    }
    jsonElement = parseLiteral();
    if (jsonElement != null) {
      return jsonElement;
    }
    System.out.println("Invalid Json element at " + position);
    return null;
  }

  private JsonObject parseJsonObject() {
    try {
      if (input.charAt(position) != '{') {
        return null;
      }
      position++;
      JsonObject jsonObject0 = new JsonObject();
      skipWhitespaces();
      if (input.charAt(position) == '}') {
        position++;
        return jsonObject0;
      }
      while (true) {
        parseJsonObjectMember(jsonObject0);
        if (input.charAt(position) == ',') {
          position++;
          continue;
        }
        break;
      }
      String className = jsonObject0.getAsString("$className");
      if (className != null) {
        JsonObject jsonObject1 = createJsonObjectByClassName(className);
        SwiftArray<String> keys = jsonObject0.keys();
        for (int i = 0; i < keys.count(); i++) {
          String key = keys.get(i);
          jsonObject1.set(key, jsonObject0.get(key));
        }
        jsonObject1.deserialize(jsonObject0);
        jsonObject0 = jsonObject1;
      }
      skipWhitespaces();
      if (input.charAt(position) == '}') {
        position++;
      }
      return jsonObject0;
    } catch (Exception e) {
      System.out.println("End of input at " + position);
      return null;
    }
  }

  protected JsonObject createJsonObjectByClassName(String className) {
    return new JsonObject();
  }

  private void parseJsonObjectMember(JsonObject jsonObject) {
    try {
      skipWhitespaces();
      String name = parseString();
      if (name == null) {
        System.out.println("JsonObject name expected at " + position);
        return;
      }
      skipWhitespaces();
      if (input.charAt(position) == ':') {
        position++;
        JsonElement jsonElement = parseJsonElement();
        jsonObject.set(name, jsonElement);
      }
    } catch (Exception e) {
      System.out.println("End of input at " + position);
    }
  }

  private JsonArray parseJsonArray() {
    try {
      if (input.charAt(position) != '[') {
        return null;
      }
      position++;
      JsonArray jsonArray = new JsonArray();
      skipWhitespaces();
      while (position < input.length()) {
        if (input.charAt(position) == ']') {
          break;
        }
        JsonElement jsonElement = parseJsonElement();
        jsonArray.add(jsonElement);
        if (input.charAt(position) != ',') {
          break;
        }
        position++;
      }
      position++;
      return jsonArray;
    } catch (Exception e) {
      System.out.println("End of input at " + position);
      return null;
    }
  }

  private String parseString() {
    try {
      if (input.charAt(position) != '"') {
        return null;
      }
      position++;
      int i = position;
      while (true) {
        i = input.indexOf('"', i);
        if (i < 0) {
          String result = input.substring(position);
          position = input.length();
          return result;
        }
        if (input.charAt(i - 1) == '\\') {
          i++;
          continue;
        }
        String result = input.substring(position, i);
        result = result.replace("\\\"", "\"");
        position = i + 1;
        return result;
      }
    } catch (Exception e) {
      System.out.println("End of input at " + position);
      return null;
    }
  }

  private JsonPrimitive parseNumber() {
    SwiftStringBuilder swiftStringBuilder = new SwiftStringBuilder();
    try {
      char character = input.charAt(position);
      if (((character >= '0') && (character <= '9')) || (character == '-')) {
        swiftStringBuilder.appendCharacter(character);
        position++;
        while (position < input.length()) {
          character = input.charAt(position);
          if (character >= '0' && character <= '9') {
            swiftStringBuilder.appendCharacter(character);
            position++;
          } else if (character == '.') {
            swiftStringBuilder.appendCharacter(character);
            position++;
          } else if (character == 'e') {
            swiftStringBuilder.appendCharacter(character);
            position++;
          } else if (character == 'E') {
            swiftStringBuilder.appendCharacter(character);
            position++;
          } else if (character == '+') {
            swiftStringBuilder.appendCharacter(character);
            position++;
          } else if (character == '-') {
            swiftStringBuilder.appendCharacter(character);
            position++;
          } else {
            break;
          }
        }
        String numberString = swiftStringBuilder.toString();

        OptionalInt optionalInt = SwiftRuntime.parseInt(numberString);
        if (optionalInt != null) {
          JsonIntegerPrimitive jsonIntegerPrimitive = new JsonIntegerPrimitive(optionalInt.value);
          delete(optionalInt);
          return jsonIntegerPrimitive;
        }

        OptionalDouble optionalDouble = SwiftRuntime.parseDouble(numberString);
        if (optionalDouble != null) {
          JsonDoublePrimitive jsonDoublePrimitive = new JsonDoublePrimitive(optionalDouble.value);
          delete(optionalDouble);
          return jsonDoublePrimitive;
        }
      } else {
        return null;
      }
    } catch (Exception exception) {
      System.out.println("End of input at " + position);
      return null;
    }
    return null;
  }

  private JsonElement parseLiteral() {
    try {
      if (input.startsWith("null", position)) {
        position += 4;
        return new JsonNull();
      }
      if (input.startsWith("false", position)) {
        position += 5;
        return new JsonBooleanPrimitive(false);
      }
      if (input.startsWith("true", position)) {
        position += 4;
        return new JsonBooleanPrimitive(true);
      }
    } catch (Exception e) {
      System.out.println("End of input at " + position);
    }
    return null;
  }

  private void skipWhitespaces() {
    while (position < input.length()) {
      char c = input.charAt(position);
      if ((c == '\t') || (c == '\n') || (c == '\r') || (c == ' ')) {
        position++;
      } else {
        break;
      }
    }
  }
}
