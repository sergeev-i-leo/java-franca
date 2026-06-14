package franca.java.parsers.html;

import franca.java.expected.StringBuffer;
import franca.java.expected.Runtime;
import franca.java.parsers.Parser;
import franca.java.parsers.json.JsonArray;
import franca.java.parsers.json.JsonObject;
import franca.java.parsers.json.JsonStringPrimitive;

public class HtmlParser extends Parser {

  // 0 for nothing, 1 for nothing with input flag, 2 for debugging
  public StringBuffer outputStringBuffer;
  private int outputSpacesNumber = 0;

  public JsonArray parse(String input, StringBuffer outputStringBuffer) {
    this.input = input;
    this.outputStringBuffer = outputStringBuffer;

    position = 0;

    JsonArray jsonArray = new JsonArray();
    parseHtmlNodeContents(null, jsonArray);

    return jsonArray;
  }

  private void parseHtmlNodeContents(String tagName, JsonArray jsonArray) {

    while (position < input.length()) {
      skipWhitespaces();

      if ((peekChar() == '<') && (peekNextChar(1) == '!')) {
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
        jsonObject.putStringValue("#comment", literalStringBuffer.getString());

        if (outputStringBuffer != null) {
          outputStringBuffer.appendChars('.', outputSpacesNumber);
          outputStringBuffer.appendString(literalStringBuffer.getString());
          outputStringBuffer.appendEndLine();
        }

        continue;
      }

      // can be part of inline element <span>, <strong>, <em>
      parseTextContents(jsonArray);

      if (peekChar() != '<') {
        // error, tag is missing
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

        if (tagName == null) {
          // root of the document
          if (outputStringBuffer != null) {
            outputStringBuffer.appendChars('.', outputSpacesNumber);
            outputStringBuffer.appendString("</ " + closingTagName + " >");
            outputStringBuffer.appendEndLine();
          }
          return;
        }

        if (closingTagName.equals(tagName)) {
          if (outputStringBuffer != null) {
            outputSpacesNumber -= 2;
            outputStringBuffer.appendChars('.', outputSpacesNumber);
            outputStringBuffer.appendString("</ " + closingTagName + " >");
            outputStringBuffer.appendEndLine();
          }
          return;
        }
      }

      JsonObject jsonObject = new JsonObject();
      jsonArray.add(jsonObject);

      skipChars(1);

      int storedPosition = position;
      parseHtmlNode(jsonObject);
      if (storedPosition == position) {
        // avoid cycling
        return;
      }
    }
  }

  public void parseHtmlNode(JsonObject jsonObject) {

    String tagName = parseTagName();

    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString("< " + tagName);
      outputStringBuffer.appendEndLine();
    }

    jsonObject.putStringValue("tagName", tagName);

    outputSpacesNumber += 2;
    parseHtmlAttributes(jsonObject);
    outputSpacesNumber -= 2;

    // self-closing tags

    if (isSelfClosingTag(tagName)) {
      if (outputStringBuffer != null) {
        outputStringBuffer.appendChars('.', outputSpacesNumber);
        outputStringBuffer.appendString("/>");
        outputStringBuffer.appendEndLine();
      }
      return;
    }

    // '>'
    skipChars(1);

    JsonArray jsonArray = new JsonArray();
    jsonObject.put("contents", jsonArray);

    outputSpacesNumber += 2;
    parseHtmlNodeContents(tagName, jsonArray);
  }

  private String parseTagName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (Character.isLetterOrDigit(peekChar()))) {
      char c = consumeChar();
      stringBuffer.appendChar(c);
    }
    return stringBuffer.getLowerCaseString();
  }

  private boolean isSelfClosingTag(String tagName) {
    skipWhitespaces();

    if ((peekChar() == '/') && (peekNextChar(1) == '>')) {
      // self-closing
      skipChars(2);
      return true;
    }

    if (tagName.equals("area")) {
    } else if (tagName.equals("img")) {
    } else if (tagName.equals("input")) {
    } else if (tagName.equals("hr")) {
    } else if (tagName.equals("link")) {
    } else if (tagName.equals("meta")) {
    } else {
      return false;
    }
    if (peekChar() == '>') {
      skipChars(1);
      return true;
    }
    return false;
  }

  private void parseHtmlAttributes(JsonObject jsonObject) {

    JsonArray attributesJsonArray = new JsonArray();
    jsonObject.put("attributes", attributesJsonArray);

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
        if (outputStringBuffer != null) {
          outputStringBuffer.appendChars('.', outputSpacesNumber);
          outputStringBuffer.appendString(attributeName);
          outputStringBuffer.appendEndLine();
        }
        continue;
      }

      skipChars(1);

      if (attributeName.equals("class")) {
        JsonArray jsonArray = new JsonArray();
        jsonObject.put("class", jsonArray);
        parseClassAttribute(jsonArray);
      } else if (attributeName.equals("style")) {
        JsonArray jsonArray = new JsonArray();
        jsonObject.put("style", jsonArray);
        parseStyleAttribute(jsonArray);
      } else {
        parseAttributeValue(attributeName, attributesJsonArray);
      }
    }
  }

  private String parseAttributeName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (isAttributeNameCharacter(peekChar()))) {
      char c = consumeChar();
      stringBuffer.appendChar(c);
    }
    if (stringBuffer.isEmpty()) {
      return null;
    }

    return stringBuffer.getLowerCaseString();
  }

  private boolean isAttributeNameCharacter(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseClassAttribute(JsonArray jsonArray) {
    skipWhitespaces();

    literalStringBuffer = new StringBuffer();

    char classValueDelimiter = peekChar();
    skipChars(1);

    while (position < input.length()) {
      if (peekChar() == classValueDelimiter) {
        if (literalStringBuffer.isNotEmpty()) {
          jsonArray.add(new JsonStringPrimitive(literalStringBuffer.getLowerCaseString()));
          if (outputStringBuffer != null) {
            outputStringBuffer.appendChars('.', outputSpacesNumber);
            outputStringBuffer.appendString("class." + literalStringBuffer.getString());
            outputStringBuffer.appendEndLine();
          }
        }
        break;
      }
      if (peekChar() == ' ') {
        if (literalStringBuffer.isNotEmpty()) {
          jsonArray.add(new JsonStringPrimitive(literalStringBuffer.getLowerCaseString()));
          if (outputStringBuffer != null) {
            outputStringBuffer.appendChars('.', outputSpacesNumber);
            outputStringBuffer.appendString("class." + literalStringBuffer.getString());
            outputStringBuffer.appendEndLine();
          }
        }
        literalStringBuffer = new StringBuffer();
        skipChars(1);
      } else {
        literalStringBuffer.appendChar(consumeChar());
      }
    }

    skipChars(1);
  }

  private void parseStyleAttribute(JsonArray jsonArray) {
    skipWhitespaces();

    char styleValueDelimiter = peekChar();
    skipChars(1);

    while (position < input.length()) {
      if (peekChar() == styleValueDelimiter) {
        break;
      }

      String styleName = parseStyleName();
      if (styleName == null) {
        return;
      }
      skipWhitespaces();
      if (peekChar() != ':') {
        break;
      }
      skipChars(1);

      parseStyleValue(styleValueDelimiter);
      if (literalStringBuffer.isEmpty()) {
        break;
      }
      JsonObject jsonObject = new JsonObject();
      jsonArray.add(jsonObject);
      jsonObject.putStringValue(styleName, literalStringBuffer.getString());

      if (outputStringBuffer != null) {
        outputStringBuffer.appendChars('.', outputSpacesNumber);
        outputStringBuffer.appendString("style." + styleName + " : " + literalStringBuffer.getString());
        outputStringBuffer.appendEndLine();
      }

      if (peekChar() != ';') {
        break;
      }

      skipChars(1);
    }

    skipChars(1);
  }

  private String parseStyleName() {
    skipWhitespaces();

    literalStringBuffer = new StringBuffer();
    while ((position < input.length()) && (isStyleNameCharacter(peekChar()))) {
      char c = consumeChar();
      literalStringBuffer.appendChar(c);
    }
    if (literalStringBuffer.isEmpty()) {
      return null;
    }
    return literalStringBuffer.getLowerCaseString();
  }

  private boolean isStyleNameCharacter(char c) {
    return (c != ':') && (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseStyleValue(char styleValueDelimiter) {
    skipWhitespaces();

    literalStringBuffer = new StringBuffer();
    boolean insideQuotes = false;
    char quoteCharacter = 0;

    while (position < input.length()) {
      char c = peekChar();

      if (c == '\\') {
        skipChars(1);
        c = consumeChar();
        literalStringBuffer.appendChar(c);
        continue;
      }

      if (c == styleValueDelimiter) {
        break;
      }

      // quotes
      if (((c == '"') || (c == '\'')) && (!insideQuotes)) {
        insideQuotes = true;
        quoteCharacter = c;
        literalStringBuffer.appendChar(consumeChar());
        continue;
      }
      if ((insideQuotes) && (c == quoteCharacter)) {
        insideQuotes = false;
        quoteCharacter = 0;
        literalStringBuffer.appendChar(consumeChar());
        continue;
      }

      // inside quotes
      if (insideQuotes) {
        literalStringBuffer.appendChar(consumeChar());
        continue;
      }

      // outside quotes
      if ((c == ';') || (c == '>') || (c == '/')) {
        break;
      }

      literalStringBuffer.appendChar(consumeChar());
    }
  }

  private void parseAttributeValue(String attributeName, JsonArray jsonArray) {
    skipWhitespaces();

    char attributeValueDelimiter = peekChar();
    if ((attributeValueDelimiter != '\"') && (attributeValueDelimiter != '\'')) {
      // not quoted
      literalStringBuffer = new StringBuffer();
      while (position < input.length()) {
        char c = peekChar();
        if ((c == '>') || (c == '/') || (isWhitespace(c))) {
          break;
        }
        literalStringBuffer.appendChar(consumeChar());
      }
      JsonObject jsonObject = new JsonObject();
      jsonArray.add(jsonObject);
      jsonObject.putStringValue(attributeName, literalStringBuffer.toString());

      if (outputStringBuffer != null) {
        outputStringBuffer.appendChars('.', outputSpacesNumber);
        outputStringBuffer.appendString(attributeName + " = " + literalStringBuffer.getString());
        outputStringBuffer.appendEndLine();
      }

      return;
    }

    skipChars(1);

    literalStringBuffer = new StringBuffer();
    while (position < input.length()) {
      char c = peekChar();
      if (c == '\\') {
        skipChars(1);
        c = consumeChar();
        literalStringBuffer.appendChar(c);
        continue;
      }
      if (c == attributeValueDelimiter) {
        skipChars(1);
        break;
      }
      if ((c == '>') || ((c == '/') && (peekNextChar(1) == '>'))) {
        break;
      }
      literalStringBuffer.appendChar(c);
      skipChars(1);
    }
    if (literalStringBuffer.isEmpty()) {
      return;
    }
    JsonObject attributeJsonObject = new JsonObject();
    jsonArray.add(attributeJsonObject);
    attributeJsonObject.putStringValue(attributeName, literalStringBuffer.toString());

    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString(attributeName + " = \"" + literalStringBuffer.getString() + "\"");
      outputStringBuffer.appendEndLine();
    }
  }

  private void parseTextContents(JsonArray jsonArray) {

    literalStringBuffer = null;

    boolean skipSpaces = true;

    while (position < input.length()) {
      if (peekChar() == '\r') {
        skipChars(1);
        if (peekChar() == '\n') {
          skipChars(1);
        }
        skipWhitespaces();
        continue;
      }
      if ((peekChar() == ' ') && (skipSpaces)) {
        skipWhitespaces();
        continue;
      }
      if (peekString("&amp;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("&");
        skipSpaces = false;
        skipChars(5);
        continue;
      }
      if (peekString("&lt;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("<");
        skipSpaces = false;
        skipChars(4);
        continue;
      }
      if (peekString("&gt;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString(">");
        skipSpaces = false;
        skipChars(4);
        continue;
      }
      if (peekString("&quot;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("\"");
        skipSpaces = false;
        skipChars(6);
        continue;
      }
      if (peekString("&#39;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("'");
        skipSpaces = false;
        skipChars(5);
        continue;
      }
      if (peekString("&Aacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Á");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&aacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("á");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&Eacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("É");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&eacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("é");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&Iacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Í");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&iacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("í");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&Oacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Ó");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&oacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("ó");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&Uacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Ú");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&uacute;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("ú");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&Ntilde;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("Ñ");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&ntilde;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("ñ");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&copy;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("©");
        skipSpaces = false;
        skipChars(8);
        continue;
      }
      if (peekString("&reg;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("®");
        skipSpaces = false;
        skipChars(5);
        continue;
      }
      if (peekString("&trade;")) {
        if (literalStringBuffer == null) {
          literalStringBuffer = new StringBuffer();
        }
        literalStringBuffer.appendString("™");
        skipSpaces = false;
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
        skipSpaces = false;
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
      if (peekString("&nbsp;")) {
        if (literalStringBuffer.isNotEmpty()) {
          appendTextJsonObject(literalStringBuffer.toString(), "#text", jsonArray);
          literalStringBuffer = null;
        }
        appendTextJsonObject("&nbsp;", "#non-breakable-space", jsonArray);
        skipSpaces = false;
        skipChars(6);
        continue;
      }
      if (peekString("<br>")) {
        if (literalStringBuffer.isNotEmpty()) {
          appendTextJsonObject(literalStringBuffer.toString(), "#text", jsonArray);
          literalStringBuffer = null;
        }
        appendTextJsonObject("<br>", "#line-break", jsonArray);
        skipSpaces = true;
        skipChars(4);
        continue;
      }
      if (peekChar() == ' ') {
        if (literalStringBuffer.isNotEmpty()) {
          appendTextJsonObject(literalStringBuffer.toString(), "#text", jsonArray);
          literalStringBuffer = null;
        }
        appendTextJsonObject(" ", "#space", jsonArray);
        skipSpaces = false;
        skipChars(1);
        continue;
      }

      char c = peekChar();
      if (c == '<') {
        break;
      }

      if (literalStringBuffer == null) {
        literalStringBuffer = new StringBuffer();
      }
      literalStringBuffer.appendChar(consumeChar());
      skipSpaces = false;
    }

    if (literalStringBuffer != null) {
      // text found
      appendTextJsonObject(literalStringBuffer.getString(), "#text", jsonArray);
    }
  }

  private void appendTextJsonObject(String text, String memberName, JsonArray jsonArray) {
    JsonObject jsonObject = new JsonObject();
    jsonArray.add(jsonObject);
    jsonObject.putStringValue(memberName, text);
    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString(memberName + " \"" + literalStringBuffer.getString() + "\"");
      outputStringBuffer.appendEndLine();
    }
  }
}
