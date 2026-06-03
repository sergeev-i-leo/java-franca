package me.swift.engine.data.html;

import me.swift.engine.contract.OptionalInt;
import me.swift.engine.contract.SwiftRuntime;
import me.swift.engine.contract.StringBuffer;
import me.swift.engine.data.Parser;
import me.swift.engine.data.json.JsonArray;
import me.swift.engine.data.json.JsonObject;
import me.swift.engine.data.json.JsonStringPrimitive;

public class HtmlParser extends Parser {

  // 0 for nothing, 1 for nothing with input checking, 2 for debugging
  public int debuggingLevel = 0;

  public JsonArray parse(String input) {
    if (input != null) {
      this.input = input;
    }

    position = 0;

    JsonArray jsonArray = new JsonArray();
    parseHtmlNodeContents(null, jsonArray);

    return jsonArray;
  }

  private void parseHtmlNodeContents(String tagName, JsonArray jsonArray) {

    while (true) {

      while ((peekCharacter() == '<') && (peekNextCharacter(1) == '!')) {
        appendTextJsonObject(jsonArray, "<!");
        parseTextContents(jsonArray);
        skipWhitespaces();
      }

      // can be part of inline element <span>, <strong>, <em>
      parseTextContents(jsonArray);

      if (position >= input.length()) {
        // html is broken
        skipCharacters(1);
        return;
      }

      if (peekCharacter() != '<') {
        // oops, tag is missing
        skipCharacters(1);
        return;
      }

      if (peekNextCharacter(1) == '/') {
        // closing tag
        skipCharacters(2);
        String closingTagName = parseTagName();
        if ((position < input.length()) && (peekCharacter() == '>')) {
          skipCharacters(1);
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
        // oops, no cycling!
        return;
      }
    }
  }

  public void parseHtmlNode(JsonArray jsonArray) {
    if (consumeCharacter() != '<') {
      // oops, goodbye cycling
      skipCharacters(1);
      return;
    }

    String tagName = parseTagName();

    if (debuggingLevel > 1) {
      System.out.println("<" + tagName + ">");
    }

    JsonObject jsonObject = new JsonObject();
    jsonArray.appendElement(jsonObject);
    jsonObject.setStringMember("tagName", tagName);
    parseHtmlAttributes(jsonObject);

    if (tagName.equals("img")) {
      // self-closing
      if ((peekCharacter() == '/') && (peekNextCharacter(1) == '>')) {
        skipCharacters(2);
      } else if (peekCharacter() == '>') {
        skipCharacters(1);
      }
      return;
    }

    if ((peekCharacter() == '/') && (peekNextCharacter(1) == '>')) {
      // self-closing
      skipCharacters(2);
      return;
    }

    // '>'
    skipCharacters(1);

    jsonArray = new JsonArray();
    jsonObject.setMember("contents", jsonArray);
    skipWhitespaces();
    parseHtmlNodeContents(tagName, jsonArray);
  }

  private String parseTagName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (Character.isLetterOrDigit(peekCharacter()))) {
      char c = consumeCharacter();
      stringBuffer.appendCharacter(c);
    }
    String string = stringBuffer.getLowerCaseString();
    delete(stringBuffer);
    return string;
  }

  private void parseHtmlAttributes(JsonObject jsonObject) {

    JsonArray attributesJsonArray = new JsonArray();
    jsonObject.setMember("attributes", attributesJsonArray);
    JsonArray styleJsonArray = new JsonArray();
    jsonObject.setMember("style", styleJsonArray);

    while ((position < input.length()) && (peekCharacter() != '>') && (peekCharacter() != '/')) {

      skipWhitespaces();

      // attribute name

      String attributeName = parseAttributeName();
      if (attributeName == null) {
        return;
      }

      skipWhitespaces();

      if (peekCharacter() != '=') {
        attributesJsonArray.appendElement(new JsonStringPrimitive(attributeName));
        if (debuggingLevel > 1) {
          System.out.println("boolean attribute found " + attributeName);
        }
        delete(attributeName);
        continue;
      }

      skipCharacters(1);

      parseAttributeValue(attributeName, attributesJsonArray, styleJsonArray);
      delete(attributeName);
    }
  }

  private String parseAttributeName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (isAttributeNameCharacter(peekCharacter()))) {
      char c = consumeCharacter();
      stringBuffer.appendCharacter(c);
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

    char attributeValueDelimiter = peekCharacter();
    if ((attributeValueDelimiter != '\"') && (attributeValueDelimiter != '\'')) {
      StringBuffer stringBuffer = new StringBuffer();
      while (position < input.length()) {
        char c = peekCharacter();
        if ((c == '>') || (c == '/') || (isWhitespace(c))) {
          break;
        }
        stringBuffer.appendCharacter(consumeCharacter());
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.appendElement(attributeJsonObject);
      attributeJsonObject.setStringMember(attributeName, stringBuffer.toString());

      if (debuggingLevel > 1) {
        System.out.println("unquoted attribute found " + attributeName + " : " + stringBuffer.getString());
      }

      delete(stringBuffer);
      return;
    }

    skipCharacters(1);

    if (!attributeName.equals("style")) {
      StringBuffer stringBuffer = new StringBuffer();
      while (position < input.length()) {
        char c = peekCharacter();
        if (c == '\\') {
          skipCharacters(1);
          c = consumeCharacter();
          stringBuffer.appendCharacter(c);
          continue;
        }
        if (c == attributeValueDelimiter) {
          skipCharacters(1);
          break;
        }
        if ((c == '>') || (c == '/')) {
          break;
        }
        stringBuffer.appendCharacter(c);
        skipCharacters(1);
      }
      if (stringBuffer.isEmpty()) {
        delete(stringBuffer);
        return;
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.appendElement(attributeJsonObject);
      attributeJsonObject.setStringMember(attributeName, stringBuffer.toString());

      if (debuggingLevel > 1) {
        System.out.println("quoted attribute found " + attributeName + " : " + stringBuffer.getString());
      }

      delete(stringBuffer);
      return;
    }

    while (position < input.length()) {
      if (peekCharacter() == attributeValueDelimiter) {
        break;
      }

      String styleName = parseStyleName();
      if (styleName == null) {
        return;
      }
      skipWhitespaces();
      if (peekCharacter() != ':') {
        delete(styleName);
        break;
      }
      skipCharacters(1);

      skipWhitespaces();
      StringBuffer stringBuffer = parseStyleValue();
      if (stringBuffer == null) {
        delete(styleName);
        break;
      }
      JsonObject styleJsonObject = new JsonObject();
      styleJsonArray.appendElement(styleJsonObject);
      styleJsonObject.setStringMember(styleName, stringBuffer.getString());

      if (debuggingLevel > 1) {
        System.out.println("style found " + styleName + " : " + stringBuffer.getString());
      }

      delete(styleName);
      delete(stringBuffer);

      skipWhitespaces();
      if (peekCharacter() != ';') {
        break;
      }

      skipCharacters(1);
    }
  }

  private String parseStyleName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (isAttributeNameCharacter(peekCharacter()))) {
      char c = consumeCharacter();
      stringBuffer.appendCharacter(c);
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
      char c = peekCharacter();

      if (c == '\\') {
        skipCharacters(1);
        c = consumeCharacter();
        stringBuffer.appendCharacter(c);
        continue;
      }

      // quotes
      if (((c == '"') || (c == '\'')) && (!insideQuotes)) {
        insideQuotes = true;
        quoteCharacter = c;
        stringBuffer.appendCharacter(consumeCharacter());
        continue;
      }
      if ((insideQuotes) && (c == quoteCharacter)) {
        insideQuotes = false;
        quoteCharacter = 0;
        stringBuffer.appendCharacter(consumeCharacter());
        continue;
      }

      // inside quotes
      if (insideQuotes) {
        stringBuffer.appendCharacter(consumeCharacter());
        continue;
      }

      // outside quotes
      if ((c == ';') || (c == '>') || (c == '/')) {
        break;
      }

      stringBuffer.appendCharacter(consumeCharacter());
    }

    if (stringBuffer.isEmpty()) {
      delete(stringBuffer);
      return null;
    }

    return stringBuffer;
  }

  private void parseTextContents(JsonArray jsonArray) {

    StringBuffer textStringBuffer = new StringBuffer();
    StringBuffer htmlLetterStringBuffer = null;

    while (position < input.length()) {
      char c = peekCharacter();
      if (c == '<') {
        if (peekString("<br>")) {
          if (textStringBuffer.isNotEmpty()) {
            appendTextJsonObject(jsonArray, textStringBuffer.toString());
            delete(textStringBuffer);
            textStringBuffer = new StringBuffer();
          }
          appendTextJsonObject(jsonArray, "<br>");
          skipCharacters(4);
          continue;
        } else if (peekNextCharacter(1) != '!') {
          break;
        }
      }

      if (c == '\r') {
        skipCharacters(1);
        if (peekCharacter() == '\n') {
          skipCharacters(1);
        }
        skipWhitespaces();
        continue;
      }

      if ((peekNextCharacter(0) == '&') && ((peekNextCharacter(1) == '#'))) {
        if (htmlLetterStringBuffer != null) {
          // html letter not finished
          textStringBuffer.appendString(htmlLetterStringBuffer.getString());
          delete(htmlLetterStringBuffer);
        }
        htmlLetterStringBuffer = new StringBuffer();
        skipCharacters(2);
        continue;
      }
      if (htmlLetterStringBuffer != null) {
        c = peekCharacter();
        if (c == ';') {
          // try to convert to char
          OptionalInt optionalInt = SwiftRuntime.parseHexInt(htmlLetterStringBuffer.getString());
          if (optionalInt == null) {
            textStringBuffer.appendString(htmlLetterStringBuffer.getString());
          } else {
            textStringBuffer.appendCharacter((char) optionalInt.value);
          }
          delete(htmlLetterStringBuffer);
          htmlLetterStringBuffer = null;
          skipCharacters(1);
          continue;
        }
        if (Character.isLetterOrDigit(c)) {
          htmlLetterStringBuffer.appendCharacter(c);
          skipCharacters(1);
          continue;
        }
        // error in html
        textStringBuffer.appendString(htmlLetterStringBuffer.getString());
        delete(htmlLetterStringBuffer);
        htmlLetterStringBuffer = null;
      }
      if (peekString("&amp;")) {
        textStringBuffer.appendString("&");
        skipCharacters(5);
        continue;
      }
      if (peekString("&lt;")) {
        textStringBuffer.appendString("<");
        skipCharacters(4);
        continue;
      }
      if (peekString("&gt;")) {
        textStringBuffer.appendString(">");
        skipCharacters(4);
        continue;
      }
      if (peekString("&quot;")) {
        textStringBuffer.appendString("\"");
        skipCharacters(6);
        continue;
      }
      if (peekString("&#39;")) {
        textStringBuffer.appendString("'");
        skipCharacters(5);
        continue;
      }
      if (peekString("&nbsp;")) {
        textStringBuffer.appendString(" ");
        skipCharacters(6);
        continue;
      }
      if (peekString("&Aacute;")) {
        textStringBuffer.appendString("Á");
        position += 8;
        continue;
      }
      if (peekString("&aacute;")) {
        textStringBuffer.appendString("á");
        position += 8;
        continue;
      }
      if (peekString("&Eacute;")) {
        textStringBuffer.appendString("É");
        position += 8;
        continue;
      }
      if (peekString("&eacute;")) {
        textStringBuffer.appendString("é");
        position += 8;
        continue;
      }
      if (peekString("&Iacute;")) {
        textStringBuffer.appendString("Í");
        position += 8;
        continue;
      }
      if (peekString("&iacute;")) {
        textStringBuffer.appendString("í");
        position += 8;
        continue;
      }
      if (peekString("&Oacute;")) {
        textStringBuffer.appendString("Ó");
        position += 8;
        continue;
      }
      if (peekString("&oacute;")) {
        textStringBuffer.appendString("ó");
        position += 8;
        continue;
      }
      if (peekString("&Uacute;")) {
        textStringBuffer.appendString("Ú");
        position += 8;
        continue;
      }
      if (peekString("&uacute;")) {
        textStringBuffer.appendString("ú");
        position += 8;
        continue;
      }
      if (peekString("&Ntilde;")) {
        textStringBuffer.appendString("Ñ");
        position += 8;
        continue;
      }
      if (peekString("&ntilde;")) {
        textStringBuffer.appendString("ñ");
        position += 8;
        continue;
      }
      if (peekString("&copy;")) {
        textStringBuffer.appendString("©");
        skipCharacters(6);
        continue;
      }
      if (peekString("&reg;")) {
        textStringBuffer.appendString("®");
        skipCharacters(5);
        continue;
      }
      if (peekString("&trade;")) {
        textStringBuffer.appendString("™");
        position += 7;
        continue;
      }
      if (peekString("&euro;")) {
        textStringBuffer.appendString("€");
        skipCharacters(6);
        continue;
      }
      if (peekString("&pound;")) {
        textStringBuffer.appendString("£");
        position += 7;
        continue;
      }
      if (peekString("&cent;")) {
        textStringBuffer.appendString("¢");
        skipCharacters(6);
        continue;
      }
      if (peekString("&yen;")) {
        textStringBuffer.appendString("¥");
        skipCharacters(5);
        continue;
      }

      textStringBuffer.appendCharacter(consumeCharacter());
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
    jsonArray.appendElement(jsonObject);
    jsonObject.setStringMember("tagName", "#text");
    jsonObject.setStringMember("value", text);
  }

  @Override
  public char peekCharacter() {
    if (debuggingLevel > 0) {
      if (peekString("<!-- debuggingLevel = 0")) {
        debuggingLevel = 0;
      } else if (peekString("<!-- debuggingLevel = 1")) {
        debuggingLevel = 1;
      } else if (peekString("<!-- debuggingLevel = 2")) {
        debuggingLevel = 2;
      }
    }
    return super.peekCharacter();
  }

  public void toStringBuffer(JsonArray jsonArray, StringBuffer stringBuffer) {
    for (int i = 0; i < jsonArray.count(); i++) {
      JsonObject jsonObject = jsonArray.getElement(i).asJsonObject();
      if (jsonObject == null) {
        continue;
      }
      String tagName = jsonObject.getStringMember("tagName");
      JsonArray contentsJsonArray = jsonObject.getJsonArrayMember("contents");
      if (tagName != null) {
        stringBuffer.appendCharacter('<');
        stringBuffer.appendString(tagName);
        stringBuffer.appendCharacter('>');
      }
      if (contentsJsonArray != null) {
        toStringBuffer(contentsJsonArray, stringBuffer);
      }
    }
  }

  public JsonArray transformToDocumentModel(JsonArray inputJsonArray) {
    JsonArray outputJsonArray = new JsonArray();
/* TODO
    String tagName = jsonObject.getStringMember("tagName");
    if ((tagName.equals("th")) || (tagName.equals("td"))) {
      String colspan = jsonObject.getStringMember("colspan");
      if (colspan != null) {
        OptionalInt optionalInt = SwiftRuntime.parseInt(colspan);
        if (optionalInt == null) {
          jsonObject.setIntegerMember("columns-count", 1);
        } else if (optionalInt.value < 1) {
          jsonObject.setIntegerMember("columns-count", 1);
          delete(optionalInt);
        } else {
          jsonObject.setIntegerMember("columns-count", optionalInt.value);
          delete(optionalInt);
        }
        delete(colspan);
      } else {
        jsonObject.setIntegerMember("columns-count", 1);
      }

      String rowspan = jsonObject.getStringMember("rowspan");
      if (rowspan != null) {
        OptionalInt optionalInt = SwiftRuntime.parseInt(rowspan);
        if (optionalInt == null) {
          jsonObject.setIntegerMember("rows-count", 1);
        } else if (optionalInt.value < 1) {
          jsonObject.setIntegerMember("rows-count", 1);
          delete(optionalInt);
        } else {
          jsonObject.setIntegerMember("rows-count", optionalInt.value);
          delete(optionalInt);
        }
        delete(rowspan);
      } else {
        jsonObject.setIntegerMember("rows-count", 1);
      }
    }*/
    return outputJsonArray;
  }
}
