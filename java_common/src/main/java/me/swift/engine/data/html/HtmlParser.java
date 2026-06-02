package me.swift.engine.data.html;

import me.swift.engine.contract.OptionalInt;
import me.swift.engine.contract.SwiftRuntime;
import me.swift.engine.contract.StringBuffer;
import me.swift.engine.data.Parser;
import me.swift.engine.data.json.JsonArray;
import me.swift.engine.data.json.JsonObject;

public class HtmlParser extends Parser {

  public JsonArray parse(String input) {
    if (input != null) {
      this.input = input;
    }

    position = 0;

    JsonArray jsonArray = new JsonArray();
    parseHtmlNodes(jsonArray);

    return jsonArray;
  }

  public void parseHtmlNodes(JsonArray viewsJsonArray) {

    while (true) {

      // look for first '<'

      StringBuffer stringBuffer = new StringBuffer();
      while ((position < input.length()) && (peekCharacter() != '<')) {
        stringBuffer.appendCharacter(consumeCharacter());
      }

      if (stringBuffer.isNotEmpty()) {
        JsonObject textNode = new JsonObject();
        textNode.setStringMember("tagName", "#text");
        textNode.setStringMember("value", stringBuffer.toString());
        viewsJsonArray.addElement(textNode);
      }

      delete(stringBuffer);

      if (position >= input.length()) {
        // not found
        return;
      }

      parseHtmlNode(viewsJsonArray);
    }
  }

  public void parseHtmlNode(JsonArray viewsJsonArray) {
    if (consumeCharacter() != '<') {
      return;
    }

    String tagName = parseTagName();
    JsonObject jsonObject = new JsonObject();
    viewsJsonArray.addElement(jsonObject);
    jsonObject.setStringMember("tagName", tagName);
    parseHtmlAttributes(jsonObject);

    if (tagName.equals("img")) {
      // self-closing
      if ((peekCharacter() == '/') && (peekNextCharacter(1) == '>')) {
        position += 2;
      } else if (peekCharacter() == '>') {
        position++;
      }
      return;
    }
    if (tagName.equals("span")) {
      parseHtmlNodeContents(tagName, viewsJsonArray);
      return;
    }

    if ((peekCharacter() == '/') && (peekNextCharacter(1) == '>')) {
      // self-closing
      position += 2;
      return;
    }

    // '>'
    position++;

    viewsJsonArray = new JsonArray();
    jsonObject.setMember("views", viewsJsonArray);
    parseHtmlNodeContents(tagName, viewsJsonArray);
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

  private void parseHtmlNodeContents(String tagName, JsonArray viewsJsonArray) {

    while (true) {
      JsonObject jsonObject = parseTextContents();
      if (jsonObject != null) {
        viewsJsonArray.addElement(jsonObject);
      }

      if (position >= input.length()) {
        // html is broken
        return;
      }

      if (peekCharacter() != '<') {
        // oops, tag is missing
        return;
      }

      // skip '<'
      position++;

      if (peekCharacter() == '/') {
        // closing tag
        position++;
        String closingTagName = parseTagName();
        if ((position < input.length()) && (peekCharacter() == '>')) {
          position++;
        }
        if (closingTagName.equals(tagName)) {
          return;
        }
      }
      int storedPosition = position;
      parseHtmlNode(viewsJsonArray);
      if (storedPosition == position) {
        // oops, cycling
        return;
      }
    }
  }

  private void parseHtmlAttributes(JsonObject jsonObject) {

    skipWhitespaces();

    while ((position < input.length()) && (peekCharacter() != '>') && (peekCharacter() != '/')) {

      // attribute name
      StringBuffer keyStringBuffer = new StringBuffer();
      while ((position < input.length()) && (isAttributeNameChar(peekCharacter()))) {
        keyStringBuffer.appendCharacter(consumeCharacter());
      }

      skipWhitespaces();

      StringBuffer valueStringBuffer = new StringBuffer();
      if (peekCharacter() == '=') {
        consumeCharacter();

        skipWhitespaces();

        char c = peekCharacter();
        if ((c == '"') || (c == '\'')) {
          // skip quote
          consumeCharacter();

          while ((position < input.length()) && (peekCharacter() != c)) {
            valueStringBuffer.appendCharacter(consumeCharacter());
          }

          // skip quote
          consumeCharacter();
        } else {
          while ((position < input.length()) && (isAttributeValueChar(peekCharacter()))) {
            valueStringBuffer.appendCharacter(consumeCharacter());
          }
        }
        if (keyStringBuffer.isEmpty()) {
          delete(keyStringBuffer);
          delete(valueStringBuffer);
          skipWhitespaces();
          continue;
        }
        String attributeName = keyStringBuffer.getLowerCaseString();
        if (attributeName.equals("style")) {
          String[] styles = valueStringBuffer.getString().split(";");
          for (String style : styles) {
            style = style.trim();
            if (style.isEmpty()) {
              continue;
            }

            String[] parts = style.split(":");
            if (parts.length < 2) {
              continue;
            }

            String name = parts[0].trim().toLowerCase();
            String value = parts[1].trim();

            jsonObject.setStringMember("style." + name, value);
          }
        } else{
          jsonObject.setStringMember(attributeName, valueStringBuffer.getString());
        }
        skipWhitespaces();
      } else {
        // boolean attribute
        if (!keyStringBuffer.isEmpty()) {
          String attributeName = keyStringBuffer.getLowerCaseString();
          jsonObject.setBooleanMember(attributeName, true);
        }
        delete(keyStringBuffer);
      }
    }

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
    }
  }

  private boolean isAttributeNameChar(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private boolean isAttributeValueChar(char c) {
    return (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private JsonObject parseTextContents() {

    StringBuffer textStringBuffer = new StringBuffer();
    StringBuffer htmlLetterStringBuffer = null;

    while (position < input.length()) {
      if (peekNextCharacter(0) == '<') {
        if (peekString("<br>")) {
          textStringBuffer.appendString("<br>");
          position += 4;
          continue;
        }
        break;
      }

      char c = peekCharacter();
      if (c == '\r') {
        consumeCharacter();
        if (peekCharacter() == '\n') {
          consumeCharacter();
        }
        textStringBuffer.appendString("<br>");
        continue;
      }
      if (c == '\n') {
        consumeCharacter();
        textStringBuffer.appendString("<br>");
        continue;
      }
      if ((peekNextCharacter(0) == '&') && ((peekNextCharacter(1) == '#'))) {
        if (htmlLetterStringBuffer != null) {
          // html letter not finished
          textStringBuffer.appendString(htmlLetterStringBuffer.getString());
          delete(htmlLetterStringBuffer);
        }
        htmlLetterStringBuffer = new StringBuffer();
        position += 2;
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
          position++;
          continue;
        }
        if (Character.isLetterOrDigit(c)) {
          htmlLetterStringBuffer.appendCharacter(c);
          position++;
          continue;
        }
        // error in html
        textStringBuffer.appendString(htmlLetterStringBuffer.getString());
        delete(htmlLetterStringBuffer);
        htmlLetterStringBuffer = null;
      }
      if (peekString("&amp;")) {
        textStringBuffer.appendString("&");
        position += 5;
        continue;
      }
      if (peekString("&lt;")) {
        textStringBuffer.appendString("<");
        position += 4;
        continue;
      }
      if (peekString("&gt;")) {
        textStringBuffer.appendString(">");
        position += 4;
        continue;
      }
      if (peekString("&quot;")) {
        textStringBuffer.appendString("\"");
        position += 6;
        continue;
      }
      if (peekString("&#39;")) {
        textStringBuffer.appendString("'");
        position += 5;
        continue;
      }
      if (peekString("&nbsp;")) {
        textStringBuffer.appendString(" ");
        position += 6;
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
        position += 6;
        continue;
      }
      if (peekString("&reg;")) {
        textStringBuffer.appendString("®");
        position += 5;
        continue;
      }
      if (peekString("&trade;")) {
        textStringBuffer.appendString("™");
        position += 7;
        continue;
      }
      if (peekString("&euro;")) {
        textStringBuffer.appendString("€");
        position += 6;
        continue;
      }
      if (peekString("&pound;")) {
        textStringBuffer.appendString("£");
        position += 7;
        continue;
      }
      if (peekString("&cent;")) {
        textStringBuffer.appendString("¢");
        position += 6;
        continue;
      }
      if (peekString("&yen;")) {
        textStringBuffer.appendString("¥");
        position += 5;
        continue;
      }

      textStringBuffer.appendCharacter(consumeCharacter());
    }

    if (htmlLetterStringBuffer != null) {
      // not completed html character
      textStringBuffer.appendString(htmlLetterStringBuffer.getString());
      delete(htmlLetterStringBuffer);
    }

    if (textStringBuffer.isEmpty()) {
      delete(textStringBuffer);
      return null;
    }

    JsonObject jsonObject = new JsonObject();
    jsonObject.setStringMember("tagName", "#text");
    jsonObject.setStringMember("value", textStringBuffer.getString());

    delete(textStringBuffer);
    return jsonObject;
  }
}
