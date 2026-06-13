package franca.java.parsers.html;

import franca.java.expected.StringBuffer;
import franca.java.expected.Runtime;
import franca.java.parsers.Parser;
import franca.java.parsers.json.JsonArray;
import franca.java.parsers.json.JsonObject;
import franca.java.parsers.json.JsonStringPrimitive;

public class HtmlParser extends Parser {

  // 0 for nothing, 1 for nothing with input flag, 2 for debugging
  public int debuggingLevel = 0;

  public JsonArray parse(String input) {
    if (this.input != null) {
      delete(this.input);
    }
    this.input = copyOf(input);

    position = 0;

    JsonArray jsonArray = new JsonArray();
    parseHtmlNodeContents(null, jsonArray);

    return jsonArray;
  }

  private void parseHtmlNodeContents(String tagName, JsonArray jsonArray) {

    while (position < input.length()) {
      skipWhitespaces();

      if ((peekChar() == '<') && (peekNextChar(1) == '!')) {
        if (literalStringBuffer != null) {
          literalStringBuffer.destroy();
        }
        literalStringBuffer = new StringBuffer();
        literalStringBuffer.appendString("<!");
        skipChars(2);
        while (position < input.length()) {
          // html comment mustn't contain >
          char c = consumeChar();
          if (c == '>') {
            literalStringBuffer.appendChar(c);
            break;
          }
          literalStringBuffer.appendChar(c);
        }
        JsonObject jsonObject = new JsonObject();
        jsonArray.add(jsonObject);
        String literal = copyOf(literalStringBuffer.getString());
        jsonObject.putStringValue("#comment", literal);
        delete(literal);
        continue;
      }

      // can be part of inline element <span>, <strong>, <em>
      parseTextContents(jsonArray);

      if (position >= input.length()) {
        // html is broken
        skipChars(1);
        return;
      }

      if (peekChar() != '<') {
        // oops, tag is missing
        skipChars(1);
        return;
      }

      if (peekNextChar(1) == '/') {
        // closing tag
        skipChars(2);
        String closingTagName = parseTagName();
        if ((position < input.length()) && (peekChar() == '>')) {
          skipChars(1);
        }

        if (debuggingLevel > 1) {
          System.out.println("</" + closingTagName + ">");
        }

        if (tagName == null) {
          // root of the document
          return;
        }
        if (closingTagName.equals(tagName)) {
          return;
        }
      }

      skipWhitespaces();

      int storedPosition = position;
      parseHtmlNode(jsonArray);
      if (storedPosition == position) {
        // avoid cycling
        return;
      }
    }
  }

  public void parseHtmlNode(JsonArray jsonArray) {
    if (consumeChar() != '<') {
      // avoid cycling
      skipChars(1);
      return;
    }

    String tagName = parseTagName();

    if (debuggingLevel > 1) {
      System.out.println("<" + tagName + ">");
    }

    JsonObject jsonObject = new JsonObject();
    jsonArray.add(jsonObject);
    jsonObject.putStringValue("tagName", tagName);
    parseHtmlAttributes(jsonObject);

    if (tagName.equals("img")) {
      // self-closing
      if ((peekChar() == '/') && (peekNextChar(1) == '>')) {
        skipChars(2);
      } else if (peekChar() == '>') {
        skipChars(1);
      }
      return;
    }

    if ((peekChar() == '/') && (peekNextChar(1) == '>')) {
      // self-closing
      skipChars(2);
      return;
    }

    // '>'
    skipChars(1);

    jsonArray = new JsonArray();
    jsonObject.put("contents", jsonArray);
    skipWhitespaces();
    parseHtmlNodeContents(tagName, jsonArray);
  }

  private String parseTagName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (Character.isLetterOrDigit(peekChar()))) {
      char c = consumeChar();
      stringBuffer.appendChar(c);
    }
    String string = stringBuffer.getLowerCaseString();
    delete(stringBuffer);
    return string;
  }

  private void parseHtmlAttributes(JsonObject jsonObject) {

    JsonArray attributesJsonArray = new JsonArray();
    jsonObject.put("attributes", attributesJsonArray);
    JsonArray styleJsonArray = new JsonArray();
    jsonObject.put("style", styleJsonArray);

    while ((position < input.length()) && (peekChar() != '>') && (peekChar() != '/')) {

      skipWhitespaces();

      // attribute name

      String attributeName = parseAttributeName();
      if (attributeName == null) {
        return;
      }

      skipWhitespaces();

      if (peekChar() != '=') {
        attributesJsonArray.add(new JsonStringPrimitive(attributeName));
        if (debuggingLevel > 1) {
          System.out.println("boolean attribute found " + attributeName);
        }
        delete(attributeName);
        continue;
      }

      skipChars(1);

      parseAttributeValue(attributeName, attributesJsonArray, styleJsonArray);
      delete(attributeName);
    }
  }

  private String parseAttributeName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (isAttributeNameCharacter(peekChar()))) {
      char c = consumeChar();
      stringBuffer.appendChar(c);
    }
    if (stringBuffer.isEmpty()) {
      delete(stringBuffer);
      return null;
    }
    String string = stringBuffer.getLowerCaseString();
    delete(stringBuffer);
    return string;
  }

  private boolean isAttributeNameCharacter(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseAttributeValue(String attributeName, JsonArray attributesJsonArray, JsonArray styleJsonArray) {
    skipWhitespaces();

    char attributeValueDelimiter = peekChar();
    if ((attributeValueDelimiter != '\"') && (attributeValueDelimiter != '\'')) {
      StringBuffer stringBuffer = new StringBuffer();
      while (position < input.length()) {
        char c = peekChar();
        if ((c == '>') || (c == '/') || (isWhitespace(c))) {
          break;
        }
        stringBuffer.appendChar(consumeChar());
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.add(attributeJsonObject);
      attributeJsonObject.putStringValue(attributeName, stringBuffer.toString());

      if (debuggingLevel > 1) {
        System.out.println("unquoted attribute found " + attributeName + " : " + stringBuffer.getString());
      }

      delete(stringBuffer);
      return;
    }

    skipChars(1);

    if (!attributeName.equals("style")) {
      StringBuffer stringBuffer = new StringBuffer();
      while (position < input.length()) {
        char c = peekChar();
        if (c == '\\') {
          skipChars(1);
          c = consumeChar();
          stringBuffer.appendChar(c);
          continue;
        }
        if (c == attributeValueDelimiter) {
          skipChars(1);
          break;
        }
        if ((c == '>') || (c == '/')) {
          break;
        }
        stringBuffer.appendChar(c);
        skipChars(1);
      }
      if (stringBuffer.isEmpty()) {
        delete(stringBuffer);
        return;
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.add(attributeJsonObject);
      attributeJsonObject.putStringValue(attributeName, stringBuffer.toString());

      if (debuggingLevel > 1) {
        System.out.println("quoted attribute found " + attributeName + " : " + stringBuffer.getString());
      }

      delete(stringBuffer);
      return;
    }

    while (position < input.length()) {
      if (peekChar() == attributeValueDelimiter) {
        break;
      }

      String styleName = parseStyleName();
      if (styleName == null) {
        return;
      }
      skipWhitespaces();
      if (peekChar() != ':') {
        delete(styleName);
        break;
      }
      skipChars(1);

      skipWhitespaces();
      StringBuffer stringBuffer = parseStyleValue();
      if (stringBuffer == null) {
        delete(styleName);
        break;
      }
      JsonObject styleJsonObject = new JsonObject();
      styleJsonArray.add(styleJsonObject);
      styleJsonObject.putStringValue(styleName, stringBuffer.getString());

      if (debuggingLevel > 1) {
        System.out.println("style found " + styleName + " : " + stringBuffer.getString());
      }

      delete(styleName);
      delete(stringBuffer);

      skipWhitespaces();
      if (peekChar() != ';') {
        break;
      }

      skipChars(1);
    }
  }

  private String parseStyleName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (isAttributeNameCharacter(peekChar()))) {
      char c = consumeChar();
      stringBuffer.appendChar(c);
    }
    if (stringBuffer.isEmpty()) {
      delete(stringBuffer);
      return null;
    }
    String string = stringBuffer.getLowerCaseString();
    delete(stringBuffer);
    return string;
  }

  private StringBuffer parseStyleValue() {
    skipWhitespaces();

    StringBuffer stringBuffer = new StringBuffer();
    boolean insideQuotes = false;
    char quoteCharacter = 0;

    while (position < input.length()) {
      char c = peekChar();

      if (c == '\\') {
        skipChars(1);
        c = consumeChar();
        stringBuffer.appendChar(c);
        continue;
      }

      // quotes
      if (((c == '"') || (c == '\'')) && (!insideQuotes)) {
        insideQuotes = true;
        quoteCharacter = c;
        stringBuffer.appendChar(consumeChar());
        continue;
      }
      if ((insideQuotes) && (c == quoteCharacter)) {
        insideQuotes = false;
        quoteCharacter = 0;
        stringBuffer.appendChar(consumeChar());
        continue;
      }

      // inside quotes
      if (insideQuotes) {
        stringBuffer.appendChar(consumeChar());
        continue;
      }

      // outside quotes
      if ((c == ';') || (c == '>') || (c == '/')) {
        break;
      }

      stringBuffer.appendChar(consumeChar());
    }

    if (stringBuffer.isEmpty()) {
      delete(stringBuffer);
      return null;
    }

    return stringBuffer;
  }

  private void parseTextContents(JsonArray jsonArray) {

    if (literalStringBuffer != null) {
      literalStringBuffer.destroy();
      literalStringBuffer = null;
    }

    boolean acceptSpaces = false;

    while (position < input.length()) {
      if (peekChar() == '\r') {
        skipChars(1);
        if (peekChar() == '\n') {
          skipChars(1);
        }
        skipWhitespaces();
        continue;
      }
      if (peekString("&amp;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("&");
        acceptSpaces = true;
        skipChars(5);
        continue;
      }
      if (peekString("&lt;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("<");
        acceptSpaces = true;
        skipChars(4);
        continue;
      }
      if (peekString("&gt;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString(">");
        acceptSpaces = true;
        skipChars(4);
        continue;
      }
      if (peekString("&quot;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("\"");
        acceptSpaces = true;
        skipChars(6);
        continue;
      }
      if (peekString("&#39;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("'");
        acceptSpaces = true;
        skipChars(5);
        continue;
      }
      if (peekString("&nbsp;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString(" ");
        acceptSpaces = true;
        skipChars(6);
        continue;
      }
      if (peekString("&Aacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Á");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&aacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("á");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&Eacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("É");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&eacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("é");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&Iacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Í");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&iacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("í");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&Oacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Ó");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&oacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("ó");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&Uacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Ú");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&uacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("ú");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&Ntilde;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Ñ");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&ntilde;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("ñ");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&copy;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("©");
        acceptSpaces = true;
        skipChars(8);
        continue;
      }
      if (peekString("&reg;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("®");
        acceptSpaces = true;
        skipChars(5);
        continue;
      }
      if (peekString("&trade;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("™");
        acceptSpaces = true;
        skipChars(7);
        continue;
      }
      if (peekString("&euro;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("€");
        skipChars(6);
        continue;
      }
      if (peekString("&pound;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("£");
        acceptSpaces = true;
        skipChars(7);
        continue;
      }
      if (peekString("&cent;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("¢");
        skipChars(6);
        continue;
      }
      if (peekString("&yen;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("¥");
        skipChars(5);
        continue;
      }
      if (peekString("&#")) {
        skipChars(2);

        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }

        StringBuffer stringBuffer = new StringBuffer();

        while (position < input.length()) {
          if (peekChar() == ';') {
            skipChars(1);
            break;
          }
          stringBuffer.appendChar(consumeChar());
        }
        Integer integer = Runtime.hexStringToInteger(stringBuffer.getString());
        if (integer != null) {
          literalStringBuffer.appendChar((char) integer.intValue());
        } else {
          integer = Runtime.stringToInteger(stringBuffer.getString());
          if (integer != null) {
            literalStringBuffer.appendChar((char) integer.intValue());
          } else {
            literalStringBuffer.appendString(stringBuffer.getString());
          }
        }
        continue;
      }
      if (peekString("<br>")) {
        if (literalStringBuffer.isNotEmpty()) {
          appendTextJsonObject(jsonArray, literalStringBuffer.toString());
          literalStringBuffer = null;
        }
        appendTextJsonObject(jsonArray, "<br>");
        acceptSpaces = false;
        skipChars(4);
        continue;
      }
      char c = peekChar();
      if (c == '<') {
        if (peekString("<br>")) {
        } else if (peekNextChar(1) != '!') {
          break;
        }
      }



      textStringBuffer.appendCharacter(consumeChar());
    }

    if (htmlLetterStringBuffer != null) {
      // not completed html character
      textStringBuffer.appendString(htmlLetterStringBuffer.getString());
      delete(htmlLetterStringBuffer);
    }

    if (textStringBuffer.isNotEmpty()) {
      if (debuggingLevel > 1) {
        System.out.println("text found " + textStringBuffer.getString());
      }
      appendTextJsonObject(jsonArray, textStringBuffer.getString());
      delete(textStringBuffer);
    }
  }

  private void appendTextJsonObject(JsonArray jsonArray, String text) {
    JsonObject jsonObject = new JsonObject();
    jsonArray.add(jsonObject);
    jsonObject.putStringValue("#text", text);
  }

  @Override
  public char peekChar() {
    if (debuggingLevel > 0) {
      if (peekString("<!-- debuggingLevel = 0")) {
        debuggingLevel = 0;
      } else if (peekString("<!-- debuggingLevel = 1")) {
        debuggingLevel = 1;
      } else if (peekString("<!-- debuggingLevel = 2")) {
        debuggingLevel = 2;
      }
    }
    return super.peekChar();
  }
}
