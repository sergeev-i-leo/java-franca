package franca.java.parsers.html;

import contracted.franca.java.ContractedStringBuffer;
import contracted.franca.java.ContractedRuntime;
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
    ContractedStringBuffer contractedStringBuffer = new ContractedStringBuffer();
    while ((position < input.length()) && (Character.isLetterOrDigit(peekCharacter()))) {
      char c = consumeCharacter();
      contractedStringBuffer.appendCharacter(c);
    }
    String string = contractedStringBuffer.getLowerCaseString();
    delete(contractedStringBuffer);
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
    ContractedStringBuffer contractedStringBuffer = new ContractedStringBuffer();
    while ((position < input.length()) && (isAttributeNameCharacter(peekCharacter()))) {
      char c = consumeCharacter();
      contractedStringBuffer.appendCharacter(c);
    }
    if (contractedStringBuffer.isEmpty()) {
      delete(contractedStringBuffer);
      return null;
    }
    String string = contractedStringBuffer.getLowerCaseString();
    delete(contractedStringBuffer);
    return string;
  }

  private boolean isAttributeNameCharacter(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseAttributeValue(String attributeName, JsonArray attributesJsonArray, JsonArray styleJsonArray) {
    skipWhitespaces();

    char attributeValueDelimiter = peekCharacter();
    if ((attributeValueDelimiter != '\"') && (attributeValueDelimiter != '\'')) {
      ContractedStringBuffer contractedStringBuffer = new ContractedStringBuffer();
      while (position < input.length()) {
        char c = peekCharacter();
        if ((c == '>') || (c == '/') || (isWhitespace(c))) {
          break;
        }
        contractedStringBuffer.appendCharacter(consumeCharacter());
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.add(attributeJsonObject);
      attributeJsonObject.putStringValue(attributeName, contractedStringBuffer.toString());

      if (debuggingLevel > 1) {
        System.out.println("unquoted attribute found " + attributeName + " : " + contractedStringBuffer.getString());
      }

      delete(contractedStringBuffer);
      return;
    }

    skipCharacters(1);

    if (!attributeName.equals("style")) {
      ContractedStringBuffer contractedStringBuffer = new ContractedStringBuffer();
      while (position < input.length()) {
        char c = peekCharacter();
        if (c == '\\') {
          skipCharacters(1);
          c = consumeCharacter();
          contractedStringBuffer.appendCharacter(c);
          continue;
        }
        if (c == attributeValueDelimiter) {
          skipCharacters(1);
          break;
        }
        if ((c == '>') || (c == '/')) {
          break;
        }
        contractedStringBuffer.appendCharacter(c);
        skipCharacters(1);
      }
      if (contractedStringBuffer.isEmpty()) {
        delete(contractedStringBuffer);
        return;
      }
      JsonObject attributeJsonObject = new JsonObject();
      attributesJsonArray.add(attributeJsonObject);
      attributeJsonObject.putStringValue(attributeName, contractedStringBuffer.toString());

      if (debuggingLevel > 1) {
        System.out.println("quoted attribute found " + attributeName + " : " + contractedStringBuffer.getString());
      }

      delete(contractedStringBuffer);
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
      ContractedStringBuffer contractedStringBuffer = parseStyleValue();
      if (contractedStringBuffer == null) {
        delete(styleName);
        break;
      }
      JsonObject styleJsonObject = new JsonObject();
      styleJsonArray.add(styleJsonObject);
      styleJsonObject.putStringValue(styleName, contractedStringBuffer.getString());

      if (debuggingLevel > 1) {
        System.out.println("style found " + styleName + " : " + contractedStringBuffer.getString());
      }

      delete(styleName);
      delete(contractedStringBuffer);

      skipWhitespaces();
      if (peekCharacter() != ';') {
        break;
      }

      skipCharacters(1);
    }
  }

  private String parseStyleName() {
    ContractedStringBuffer contractedStringBuffer = new ContractedStringBuffer();
    while ((position < input.length()) && (isAttributeNameCharacter(peekCharacter()))) {
      char c = consumeCharacter();
      contractedStringBuffer.appendCharacter(c);
    }
    if (contractedStringBuffer.isEmpty()) {
      delete(contractedStringBuffer);
      return null;
    }
    String string = contractedStringBuffer.getLowerCaseString();
    delete(contractedStringBuffer);
    return string;
  }

  private ContractedStringBuffer parseStyleValue() {
    skipWhitespaces();

    ContractedStringBuffer contractedStringBuffer = new ContractedStringBuffer();
    boolean insideQuotes = false;
    char quoteCharacter = 0;

    while (position < input.length()) {
      char c = peekCharacter();

      if (c == '\\') {
        skipCharacters(1);
        c = consumeCharacter();
        contractedStringBuffer.appendCharacter(c);
        continue;
      }

      // quotes
      if (((c == '"') || (c == '\'')) && (!insideQuotes)) {
        insideQuotes = true;
        quoteCharacter = c;
        contractedStringBuffer.appendCharacter(consumeCharacter());
        continue;
      }
      if ((insideQuotes) && (c == quoteCharacter)) {
        insideQuotes = false;
        quoteCharacter = 0;
        contractedStringBuffer.appendCharacter(consumeCharacter());
        continue;
      }

      // inside quotes
      if (insideQuotes) {
        contractedStringBuffer.appendCharacter(consumeCharacter());
        continue;
      }

      // outside quotes
      if ((c == ';') || (c == '>') || (c == '/')) {
        break;
      }

      contractedStringBuffer.appendCharacter(consumeCharacter());
    }

    if (contractedStringBuffer.isEmpty()) {
      delete(contractedStringBuffer);
      return null;
    }

    return contractedStringBuffer;
  }

  private void parseTextContents(JsonArray jsonArray) {

    ContractedStringBuffer textContractedStringBuffer = new ContractedStringBuffer();
    ContractedStringBuffer htmlLetterContractedStringBuffer = null;

    while (position < input.length()) {
      char c = peekCharacter();
      if (c == '<') {
        if (peekString("<br>")) {
          if (textContractedStringBuffer.isNotEmpty()) {
            appendTextJsonObject(jsonArray, textContractedStringBuffer.toString());
            delete(textContractedStringBuffer);
            textContractedStringBuffer = new ContractedStringBuffer();
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
        if (htmlLetterContractedStringBuffer != null) {
          // html letter not finished
          textContractedStringBuffer.appendString(htmlLetterContractedStringBuffer.getString());
          delete(htmlLetterContractedStringBuffer);
        }
        htmlLetterContractedStringBuffer = new ContractedStringBuffer();
        skipCharacters(2);
        continue;
      }
      if (htmlLetterContractedStringBuffer != null) {
        c = peekCharacter();
        if (c == ';') {
          // try to convert to char
          Integer parsedInteger = ContractedRuntime.parseHexInt(htmlLetterContractedStringBuffer.getString());
          if (parsedInteger == null) {
            textContractedStringBuffer.appendString(htmlLetterContractedStringBuffer.getString());
          } else {
            textContractedStringBuffer.appendCharacter((char) parsedInteger.intValue());
          }
          delete(htmlLetterContractedStringBuffer);
          htmlLetterContractedStringBuffer = null;
          skipCharacters(1);
          continue;
        }
        if (Character.isLetterOrDigit(c)) {
          htmlLetterContractedStringBuffer.appendCharacter(c);
          skipCharacters(1);
          continue;
        }
        // error in html
        textContractedStringBuffer.appendString(htmlLetterContractedStringBuffer.getString());
        delete(htmlLetterContractedStringBuffer);
        htmlLetterContractedStringBuffer = null;
      }
      if (peekString("&amp;")) {
        textContractedStringBuffer.appendString("&");
        skipCharacters(5);
        continue;
      }
      if (peekString("&lt;")) {
        textContractedStringBuffer.appendString("<");
        skipCharacters(4);
        continue;
      }
      if (peekString("&gt;")) {
        textContractedStringBuffer.appendString(">");
        skipCharacters(4);
        continue;
      }
      if (peekString("&quot;")) {
        textContractedStringBuffer.appendString("\"");
        skipCharacters(6);
        continue;
      }
      if (peekString("&#39;")) {
        textContractedStringBuffer.appendString("'");
        skipCharacters(5);
        continue;
      }
      if (peekString("&nbsp;")) {
        textContractedStringBuffer.appendString(" ");
        skipCharacters(6);
        continue;
      }
      if (peekString("&Aacute;")) {
        textContractedStringBuffer.appendString("Á");
        position += 8;
        continue;
      }
      if (peekString("&aacute;")) {
        textContractedStringBuffer.appendString("á");
        position += 8;
        continue;
      }
      if (peekString("&Eacute;")) {
        textContractedStringBuffer.appendString("É");
        position += 8;
        continue;
      }
      if (peekString("&eacute;")) {
        textContractedStringBuffer.appendString("é");
        position += 8;
        continue;
      }
      if (peekString("&Iacute;")) {
        textContractedStringBuffer.appendString("Í");
        position += 8;
        continue;
      }
      if (peekString("&iacute;")) {
        textContractedStringBuffer.appendString("í");
        position += 8;
        continue;
      }
      if (peekString("&Oacute;")) {
        textContractedStringBuffer.appendString("Ó");
        position += 8;
        continue;
      }
      if (peekString("&oacute;")) {
        textContractedStringBuffer.appendString("ó");
        position += 8;
        continue;
      }
      if (peekString("&Uacute;")) {
        textContractedStringBuffer.appendString("Ú");
        position += 8;
        continue;
      }
      if (peekString("&uacute;")) {
        textContractedStringBuffer.appendString("ú");
        position += 8;
        continue;
      }
      if (peekString("&Ntilde;")) {
        textContractedStringBuffer.appendString("Ñ");
        position += 8;
        continue;
      }
      if (peekString("&ntilde;")) {
        textContractedStringBuffer.appendString("ñ");
        position += 8;
        continue;
      }
      if (peekString("&copy;")) {
        textContractedStringBuffer.appendString("©");
        skipCharacters(6);
        continue;
      }
      if (peekString("&reg;")) {
        textContractedStringBuffer.appendString("®");
        skipCharacters(5);
        continue;
      }
      if (peekString("&trade;")) {
        textContractedStringBuffer.appendString("™");
        position += 7;
        continue;
      }
      if (peekString("&euro;")) {
        textContractedStringBuffer.appendString("€");
        skipCharacters(6);
        continue;
      }
      if (peekString("&pound;")) {
        textContractedStringBuffer.appendString("£");
        position += 7;
        continue;
      }
      if (peekString("&cent;")) {
        textContractedStringBuffer.appendString("¢");
        skipCharacters(6);
        continue;
      }
      if (peekString("&yen;")) {
        textContractedStringBuffer.appendString("¥");
        skipCharacters(5);
        continue;
      }

      textContractedStringBuffer.appendCharacter(consumeCharacter());
    }

    if (htmlLetterContractedStringBuffer != null) {
      // not completed html character
      textContractedStringBuffer.appendString(htmlLetterContractedStringBuffer.getString());
      delete(htmlLetterContractedStringBuffer);
    }

    if (textContractedStringBuffer.isNotEmpty()) {
      if (debuggingLevel > 1) {
        System.out.println("text found " + textContractedStringBuffer.getString());
      }
      appendTextJsonObject(jsonArray, textContractedStringBuffer.getString());
      delete(textContractedStringBuffer);
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

  public void toStringBuffer(JsonArray jsonArray, ContractedStringBuffer contractedStringBuffer) {
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jsonObject = jsonArray.getJsonObject(i);
      if (jsonObject == null) {
        continue;
      }
      String tagName = jsonObject.getStringValue("tagName");
      JsonArray contentsJsonArray = jsonObject.getJsonArray("contents");
      if (tagName != null) {
        contractedStringBuffer.appendCharacter('<');
        contractedStringBuffer.appendString(tagName);
        contractedStringBuffer.appendCharacter('>');
      }
      if (contentsJsonArray != null) {
        toStringBuffer(contentsJsonArray, contractedStringBuffer);
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
