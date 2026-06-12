package franca.java.parsers.html;

import franca.java.expected.ExpectedStringBuilder;
import franca.java.expected.ExpectedRuntime;
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
        // no cycling!
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
    jsonArray.add(jsonObject);
    jsonObject.putStringValue("tagName", tagName);
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
    jsonObject.put("contents", jsonArray);
    skipWhitespaces();
    parseHtmlNodeContents(tagName, jsonArray);
  }

  private String parseTagName() {
    ExpectedStringBuilder expectedStringBuilder = new ExpectedStringBuilder();
    while ((position < input.length()) && (Character.isLetterOrDigit(peekCharacter()))) {
      char c = consumeCharacter();
      expectedStringBuilder.appendCharacter(c);
    }
    String string = expectedStringBuilder.getLowerCaseString();
    delete(expectedStringBuilder);
    return string;
  }

  private void parseHtmlAttributes(JsonObject jsonObject) {

    JsonArray attributesJsonArray = new JsonArray();
    jsonObject.put("attributes", attributesJsonArray);
    JsonArray styleJsonArray = new JsonArray();
    jsonObject.put("style", styleJsonArray);

    while ((position < input.length()) && (peekCharacter() != '>') && (peekCharacter() != '/')) {

      skipWhitespaces();

      // attribute name

      String attributeName = parseAttributeName();
      if (attributeName == null) {
        return;
      }

      skipWhitespaces();

      if (peekCharacter() != '=') {
        attributesJsonArray.add(new JsonStringPrimitive(attributeName));
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
    ExpectedStringBuilder expectedStringBuilder = new ExpectedStringBuilder();
    while ((position < input.length()) && (isAttributeNameCharacter(peekCharacter()))) {
      char c = consumeCharacter();
      expectedStringBuilder.appendCharacter(c);
    }
    if (expectedStringBuilder.isEmpty()) {
      delete(expectedStringBuilder);
      return null;
    }
    String string = expectedStringBuilder.getLowerCaseString();
    delete(expectedStringBuilder);
    return string;
  }

  private boolean isAttributeNameCharacter(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseAttributeValue(String attributeName, JsonArray attributesJsonArray, JsonArray styleJsonArray) {
    skipWhitespaces();

    char attributeValueDelimiter = peekCharacter();
    if ((attributeValueDelimiter != '\"') && (attributeValueDelimiter != '\'')) {
      ExpectedStringBuilder expectedStringBuilder = new ExpectedStringBuilder();
      while (position < input.length()) {
        char c = peekCharacter();
        if ((c == '>') || (c == '/') || (isWhitespace(c))) {
          break;
        }
        expectedStringBuilder.appendCharacter(consumeCharacter());
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.add(attributeJsonObject);
      attributeJsonObject.putStringValue(attributeName, expectedStringBuilder.toString());

      if (debuggingLevel > 1) {
        System.out.println("unquoted attribute found " + attributeName + " : " + expectedStringBuilder.getString());
      }

      delete(expectedStringBuilder);
      return;
    }

    skipCharacters(1);

    if (!attributeName.equals("style")) {
      ExpectedStringBuilder expectedStringBuilder = new ExpectedStringBuilder();
      while (position < input.length()) {
        char c = peekCharacter();
        if (c == '\\') {
          skipCharacters(1);
          c = consumeCharacter();
          expectedStringBuilder.appendCharacter(c);
          continue;
        }
        if (c == attributeValueDelimiter) {
          skipCharacters(1);
          break;
        }
        if ((c == '>') || (c == '/')) {
          break;
        }
        expectedStringBuilder.appendCharacter(c);
        skipCharacters(1);
      }
      if (expectedStringBuilder.isEmpty()) {
        delete(expectedStringBuilder);
        return;
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.add(attributeJsonObject);
      attributeJsonObject.putStringValue(attributeName, expectedStringBuilder.toString());

      if (debuggingLevel > 1) {
        System.out.println("quoted attribute found " + attributeName + " : " + expectedStringBuilder.getString());
      }

      delete(expectedStringBuilder);
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
      ExpectedStringBuilder expectedStringBuilder = parseStyleValue();
      if (expectedStringBuilder == null) {
        delete(styleName);
        break;
      }
      JsonObject styleJsonObject = new JsonObject();
      styleJsonArray.add(styleJsonObject);
      styleJsonObject.putStringValue(styleName, expectedStringBuilder.getString());

      if (debuggingLevel > 1) {
        System.out.println("style found " + styleName + " : " + expectedStringBuilder.getString());
      }

      delete(styleName);
      delete(expectedStringBuilder);

      skipWhitespaces();
      if (peekCharacter() != ';') {
        break;
      }

      skipCharacters(1);
    }
  }

  private String parseStyleName() {
    ExpectedStringBuilder expectedStringBuilder = new ExpectedStringBuilder();
    while ((position < input.length()) && (isAttributeNameCharacter(peekCharacter()))) {
      char c = consumeCharacter();
      expectedStringBuilder.appendCharacter(c);
    }
    if (expectedStringBuilder.isEmpty()) {
      delete(expectedStringBuilder);
      return null;
    }
    String string = expectedStringBuilder.getLowerCaseString();
    delete(expectedStringBuilder);
    return string;
  }

  private ExpectedStringBuilder parseStyleValue() {
    skipWhitespaces();

    ExpectedStringBuilder expectedStringBuilder = new ExpectedStringBuilder();
    boolean insideQuotes = false;
    char quoteCharacter = 0;

    while (position < input.length()) {
      char c = peekCharacter();

      if (c == '\\') {
        skipCharacters(1);
        c = consumeCharacter();
        expectedStringBuilder.appendCharacter(c);
        continue;
      }

      // quotes
      if (((c == '"') || (c == '\'')) && (!insideQuotes)) {
        insideQuotes = true;
        quoteCharacter = c;
        expectedStringBuilder.appendCharacter(consumeCharacter());
        continue;
      }
      if ((insideQuotes) && (c == quoteCharacter)) {
        insideQuotes = false;
        quoteCharacter = 0;
        expectedStringBuilder.appendCharacter(consumeCharacter());
        continue;
      }

      // inside quotes
      if (insideQuotes) {
        expectedStringBuilder.appendCharacter(consumeCharacter());
        continue;
      }

      // outside quotes
      if ((c == ';') || (c == '>') || (c == '/')) {
        break;
      }

      expectedStringBuilder.appendCharacter(consumeCharacter());
    }

    if (expectedStringBuilder.isEmpty()) {
      delete(expectedStringBuilder);
      return null;
    }

    return expectedStringBuilder;
  }

  private void parseTextContents(JsonArray jsonArray) {

    ExpectedStringBuilder textExpectedStringBuilder = new ExpectedStringBuilder();
    ExpectedStringBuilder htmlLetterExpectedStringBuilder = null;

    while (position < input.length()) {
      char c = peekCharacter();
      if (c == '<') {
        if (peekString("<br>")) {
          if (textExpectedStringBuilder.isNotEmpty()) {
            appendTextJsonObject(jsonArray, textExpectedStringBuilder.toString());
            delete(textExpectedStringBuilder);
            textExpectedStringBuilder = new ExpectedStringBuilder();
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
        if (htmlLetterExpectedStringBuilder != null) {
          // html letter not finished
          textExpectedStringBuilder.appendString(htmlLetterExpectedStringBuilder.getString());
          delete(htmlLetterExpectedStringBuilder);
        }
        htmlLetterExpectedStringBuilder = new ExpectedStringBuilder();
        skipCharacters(2);
        continue;
      }
      if (htmlLetterExpectedStringBuilder != null) {
        c = peekCharacter();
        if (c == ';') {
          // try to convert to char
          Integer parsedInteger = ExpectedRuntime.parseHexInt(htmlLetterExpectedStringBuilder.getString());
          if (parsedInteger == null) {
            textExpectedStringBuilder.appendString(htmlLetterExpectedStringBuilder.getString());
          } else {
            textExpectedStringBuilder.appendCharacter((char) parsedInteger.intValue());
          }
          delete(htmlLetterExpectedStringBuilder);
          htmlLetterExpectedStringBuilder = null;
          skipCharacters(1);
          continue;
        }
        if (Character.isLetterOrDigit(c)) {
          htmlLetterExpectedStringBuilder.appendCharacter(c);
          skipCharacters(1);
          continue;
        }
        // error in html
        textExpectedStringBuilder.appendString(htmlLetterExpectedStringBuilder.getString());
        delete(htmlLetterExpectedStringBuilder);
        htmlLetterExpectedStringBuilder = null;
      }
      if (peekString("&amp;")) {
        textExpectedStringBuilder.appendString("&");
        skipCharacters(5);
        continue;
      }
      if (peekString("&lt;")) {
        textExpectedStringBuilder.appendString("<");
        skipCharacters(4);
        continue;
      }
      if (peekString("&gt;")) {
        textExpectedStringBuilder.appendString(">");
        skipCharacters(4);
        continue;
      }
      if (peekString("&quot;")) {
        textExpectedStringBuilder.appendString("\"");
        skipCharacters(6);
        continue;
      }
      if (peekString("&#39;")) {
        textExpectedStringBuilder.appendString("'");
        skipCharacters(5);
        continue;
      }
      if (peekString("&nbsp;")) {
        textExpectedStringBuilder.appendString(" ");
        skipCharacters(6);
        continue;
      }
      if (peekString("&Aacute;")) {
        textExpectedStringBuilder.appendString("Á");
        position += 8;
        continue;
      }
      if (peekString("&aacute;")) {
        textExpectedStringBuilder.appendString("á");
        position += 8;
        continue;
      }
      if (peekString("&Eacute;")) {
        textExpectedStringBuilder.appendString("É");
        position += 8;
        continue;
      }
      if (peekString("&eacute;")) {
        textExpectedStringBuilder.appendString("é");
        position += 8;
        continue;
      }
      if (peekString("&Iacute;")) {
        textExpectedStringBuilder.appendString("Í");
        position += 8;
        continue;
      }
      if (peekString("&iacute;")) {
        textExpectedStringBuilder.appendString("í");
        position += 8;
        continue;
      }
      if (peekString("&Oacute;")) {
        textExpectedStringBuilder.appendString("Ó");
        position += 8;
        continue;
      }
      if (peekString("&oacute;")) {
        textExpectedStringBuilder.appendString("ó");
        position += 8;
        continue;
      }
      if (peekString("&Uacute;")) {
        textExpectedStringBuilder.appendString("Ú");
        position += 8;
        continue;
      }
      if (peekString("&uacute;")) {
        textExpectedStringBuilder.appendString("ú");
        position += 8;
        continue;
      }
      if (peekString("&Ntilde;")) {
        textExpectedStringBuilder.appendString("Ñ");
        position += 8;
        continue;
      }
      if (peekString("&ntilde;")) {
        textExpectedStringBuilder.appendString("ñ");
        position += 8;
        continue;
      }
      if (peekString("&copy;")) {
        textExpectedStringBuilder.appendString("©");
        skipCharacters(6);
        continue;
      }
      if (peekString("&reg;")) {
        textExpectedStringBuilder.appendString("®");
        skipCharacters(5);
        continue;
      }
      if (peekString("&trade;")) {
        textExpectedStringBuilder.appendString("™");
        position += 7;
        continue;
      }
      if (peekString("&euro;")) {
        textExpectedStringBuilder.appendString("€");
        skipCharacters(6);
        continue;
      }
      if (peekString("&pound;")) {
        textExpectedStringBuilder.appendString("£");
        position += 7;
        continue;
      }
      if (peekString("&cent;")) {
        textExpectedStringBuilder.appendString("¢");
        skipCharacters(6);
        continue;
      }
      if (peekString("&yen;")) {
        textExpectedStringBuilder.appendString("¥");
        skipCharacters(5);
        continue;
      }

      textExpectedStringBuilder.appendCharacter(consumeCharacter());
    }

    if (htmlLetterExpectedStringBuilder != null) {
      // not completed html character
      textExpectedStringBuilder.appendString(htmlLetterExpectedStringBuilder.getString());
      delete(htmlLetterExpectedStringBuilder);
    }

    if (textExpectedStringBuilder.isNotEmpty()) {
      if (debuggingLevel > 1) {
        System.out.println("text found " + textExpectedStringBuilder.getString());
      }
      appendTextJsonObject(jsonArray, textExpectedStringBuilder.getString());
      delete(textExpectedStringBuilder);
    }
  }

  private void appendTextJsonObject(JsonArray jsonArray, String text) {
    JsonObject jsonObject = new JsonObject();
    jsonArray.add(jsonObject);
    jsonObject.putStringValue("tagName", "#text");
    jsonObject.putStringValue("value", text);
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

  public void toStringBuffer(JsonArray jsonArray, ExpectedStringBuilder expectedStringBuilder) {
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jsonObject = jsonArray.getJsonObject(i);
      if (jsonObject == null) {
        continue;
      }
      String tagName = jsonObject.getStringValue("tagName");
      JsonArray contentsJsonArray = jsonObject.getJsonArray("contents");
      if (tagName != null) {
        expectedStringBuilder.appendCharacter('<');
        expectedStringBuilder.appendString(tagName);
        expectedStringBuilder.appendCharacter('>');
      }
      if (contentsJsonArray != null) {
        toStringBuffer(contentsJsonArray, expectedStringBuilder);
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
