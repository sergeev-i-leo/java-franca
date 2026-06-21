package franca.java.data.html;

import java.util.ArrayList;

import franca.java.expected.BufferedString;
import franca.java.expected.ExpectedRuntime;
import franca.java.data.Parser;
import franca.java.data.json.JsonArray;
import franca.java.data.json.JsonObject;
import franca.java.data.json.JsonStringPrimitive;
import franca.java.office.document.Block;
import franca.java.office.document.factory.DocumentFactory;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.TextBlock;

public class HtmlParser extends Parser {

  private Block bodyBlock = null;

  public Block parse(String input) {
    this.input = input;
    inputPosition = 0;

    bodyBlock = null;

    Block block = new Block();
    parseHtmlNodeContents(block);

    if (bodyBlock != null) {
      return bodyBlock;
    }
    return block;
  }

  private void parseHtmlNodeContents(Block parentBlock) {

    while (inputPosition < input.length()) {
      skipWhitespaces();

      if (peekString("<!--")) {
        // skip comment
        skipChars(4);
        while (inputPosition < input.length()) {
          if (peekString("-->")) {
            skipLine();
            break;
          }
          skipChars(1);
        }
        continue;
      } else if (peekString("<!")) {
        // skip <!DOCTYPE
        skipLine();
        continue;
      }

      // can be part of inline element <span>, <strong>, <em>
      ArrayList<JsonObject> styleJsonObjects = new ArrayList<>();
      parseHtmlTextContents(parentBlock, styleJsonObjects);

      if (peekChar() != '<') {
        // corrupted html
        skipChars(1);
        return;
      }

      int storedPosition = inputPosition;

      skipChars(1);

      skipWhitespaces();

      if (peekChar() == '/') {
        // closing tag
        inputPosition = storedPosition;
        return;
      }

      inputPosition = storedPosition;

      Block block = parseHtmlNode();
      if (block != null) {
        parentBlock.addBlock(block);
      } else {
        // corrupted html
        skipChars(1);
      }
    }
  }

  public Block parseHtmlNode() {

    if (peekChar() != '<') {
      // not a node tag is missing
      skipChars(1);
      return null;
    }

    skipChars(1);

    skipWhitespaces();

    String tagName = parseTagName();
    Block block = DocumentFactory.createBlockByTagName(tagName);
    if (tagName.equals("body")) {
      bodyBlock = block;
    }

    parseHtmlAttributes(block);

    skipWhitespaces();

    boolean needsHtmlNodeContents = true;

    if (peekChar() == '/') {
      needsHtmlNodeContents = false;
      skipChars(1);
      skipWhitespaces();
    }
    if (peekChar() != '>') {
    } else if (DocumentFactory.htmlTagIsSelfClosing(tagName)) {
      needsHtmlNodeContents = false;
    }

    // '>'

    skipChars(1);

    if (!needsHtmlNodeContents) {
      return block;
    }

    parseHtmlNodeContents(block);

    // expect closing tag

    skipWhitespaces();

    if (peekChar() != '<') {
      // corrupted html
      skipChars(1);
      return block;
    }

    skipChars(1);

    if (peekChar() != '/') {
      // corrupted html
      skipChars(1);
      return block;
    }

    skipChars(1);

    // skip closing tag, don't check matching

    parseTagName();

    skipWhitespaces();

    // '>' or corrupted html

    skipChars(1);

    return block;
  }

  private String parseTagName() {
    skipWhitespaces();

    BufferedString bufferedString = new BufferedString();
    while ((inputPosition < input.length()) && (Character.isLetterOrDigit(peekChar()))) {
      char c = consumeChar();
      bufferedString.appendChar(c);
    }
    return bufferedString.getLowerCaseString();
  }

  private void parseHtmlAttributes(Block targetBlock) {

    while ((inputPosition < input.length()) && (peekChar() != '>') && (peekChar() != '/')) {
      skipWhitespaces();

      // attribute name

      String attributeName = parseAttributeName();
      if (attributeName == null) {
        return;
      }

      skipWhitespaces();

      if (peekChar() != '=') {
        targetBlock.attributesJsonArray.add(new JsonStringPrimitive(attributeName));
        continue;
      }

      skipChars(1);

      if (attributeName.equals("class")) {
        parseClassAttribute(targetBlock.classesJsonArray);
      } else if (attributeName.equals("style")) {
        parseStyleAttribute(targetBlock.styleJsonObject);
      } else {
        parseAttributeValue(attributeName, targetBlock.attributesJsonArray);
      }
    }
  }

  private String parseAttributeName() {
    literalBufferedString = new BufferedString();
    while ((inputPosition < input.length()) && (isAttributeNameCharacter(peekChar()))) {
      char c = consumeChar();
      literalBufferedString.appendChar(c);
    }
    if (literalBufferedString.isEmpty()) {
      return null;
    }

    return literalBufferedString.getLowerCaseString();
  }

  private boolean isAttributeNameCharacter(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseClassAttribute(JsonArray classesJsonArray) {
    skipWhitespaces();

    literalBufferedString = new BufferedString();

    char delimiter = peekChar();
    skipChars(1);

    while (inputPosition < input.length()) {
      if (peekChar() == delimiter) {
        break;
      }
      if (peekChar() == ' ') {
        if (literalBufferedString.isNotEmpty()) {
          classesJsonArray.add(new JsonStringPrimitive(literalBufferedString.getLowerCaseString()));
        }
        literalBufferedString.clear();
        skipChars(1);
      } else {
        literalBufferedString.appendChar(consumeChar());
      }
    }
    if (literalBufferedString.isNotEmpty()) {
      classesJsonArray.add(new JsonStringPrimitive(literalBufferedString.getLowerCaseString()));
    }

    skipChars(1);
  }

  private void parseStyleAttribute(JsonObject styleJsonObject) {
    skipWhitespaces();

    char delimiter = peekChar();
    skipChars(1);

    while (inputPosition < input.length()) {
      if (peekChar() == delimiter) {
        break;
      }

      String name = parseStyleName();
      if (name == null) {
        return;
      }
      skipWhitespaces();
      if (peekChar() != ':') {
        break;
      }
      skipChars(1);

      parseStyleValue(delimiter);
      if (literalBufferedString.isEmpty()) {
        break;
      }

      String value = literalBufferedString.getString();
      styleJsonObject.putStringValue(name, value);

      if (peekChar() != ';') {
        break;
      }

      skipChars(1);
    }

    skipChars(1);
  }

  private String parseStyleName() {
    skipWhitespaces();

    literalBufferedString = new BufferedString();
    while ((inputPosition < input.length()) && (isStyleNameCharacter(peekChar()))) {
      char c = consumeChar();
      literalBufferedString.appendChar(c);
    }
    if (literalBufferedString.isEmpty()) {
      return null;
    }
    return literalBufferedString.getLowerCaseString();
  }

  private boolean isStyleNameCharacter(char c) {
    return (c != ':') && (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseStyleValue(char styleValueDelimiter) {
    skipWhitespaces();

    literalBufferedString = new BufferedString();
    boolean insideQuotes = false;
    char quoteCharacter = 0;

    while (inputPosition < input.length()) {
      char c = peekChar();

      if (c == '\\') {
        skipChars(1);
        c = consumeChar();
        literalBufferedString.appendChar(c);
        continue;
      }

      if (c == styleValueDelimiter) {
        break;
      }

      // quotes
      if (((c == '"') || (c == '\'')) && (!insideQuotes)) {
        insideQuotes = true;
        quoteCharacter = c;
        literalBufferedString.appendChar(consumeChar());
        continue;
      }
      if ((insideQuotes) && (c == quoteCharacter)) {
        insideQuotes = false;
        quoteCharacter = 0;
        literalBufferedString.appendChar(consumeChar());
        continue;
      }

      // inside quotes
      if (insideQuotes) {
        literalBufferedString.appendChar(consumeChar());
        continue;
      }

      // outside quotes
      if ((c == ';') || (c == '>') || (c == '/')) {
        break;
      }

      literalBufferedString.appendChar(consumeChar());
    }
  }

  private void parseAttributeValue(String attributeName, JsonArray jsonArray) {
    skipWhitespaces();

    char attributeValueDelimiter = peekChar();
    if ((attributeValueDelimiter != '\"') && (attributeValueDelimiter != '\'')) {
      // not quoted
      literalBufferedString = new BufferedString();
      while (inputPosition < input.length()) {
        char c = peekChar();
        if ((c == '>') || (c == '/') || (isWhitespace(c))) {
          break;
        }
        literalBufferedString.appendChar(consumeChar());
      }
      JsonObject jsonObject = new JsonObject();
      jsonArray.add(jsonObject);
      jsonObject.putStringValue("name", attributeName);
      jsonObject.putStringValue("value", literalBufferedString.getString());

      return;
    }

    skipChars(1);

    literalBufferedString = new BufferedString();
    while (inputPosition < input.length()) {
      char c = peekChar();
      if (c == '\\') {
        skipChars(1);
        c = consumeChar();
        literalBufferedString.appendChar(c);
        continue;
      }
      if (c == attributeValueDelimiter) {
        skipChars(1);
        break;
      }
      if ((c == '>') || ((c == '/') && (peekNextChar(1) == '>'))) {
        break;
      }
      literalBufferedString.appendChar(c);
      skipChars(1);
    }
    if (literalBufferedString.isEmpty()) {
      return;
    }
    JsonObject jsonObject = new JsonObject();
    jsonArray.add(jsonObject);
    jsonObject.putStringValue("name", attributeName);
    jsonObject.putStringValue("quoted-value", literalBufferedString.getString());
  }

  public void parseHtmlTextContents(Block parentBlock, ArrayList<JsonObject> styleJsonObjects) {

    literalBufferedString = new BufferedString();

    // for trailing spaces
    int spacesCount = -1;

    while (inputPosition < input.length()) {
      if (peekLineEnd()) {
        skipLineEnd();
        // set spaces to leading
        spacesCount = -1;
        continue;
      }

      JsonObject styleJsonObject;
      if (styleJsonObjects.isEmpty()) {
        styleJsonObject = null;
      } else {
        styleJsonObject = styleJsonObjects.get(styleJsonObjects.size() - 1);
      }

      if (parseHtmlTextContentsStyle(styleJsonObjects)) {
        if (spacesCount > 0) {
          // we accumulated spaces
          parentBlock = appendSpaceBlocks(parentBlock, spacesCount, styleJsonObject);
        } else if (literalBufferedString.isNotEmpty()) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), styleJsonObject);
        }
        literalBufferedString.clear();
        spacesCount = 0;
        continue;
      }

      if (peekString("<br>")) {
        if (literalBufferedString.isNotEmpty()) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), styleJsonObject);
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_LINE_BREAK, "", styleJsonObject);
        skipChars(4);
        literalBufferedString.clear();
        continue;
      }

      if (peekChar() == '<') {
        break;
      }

      if (peekChar() == ' ') {
        if (spacesCount < 0) {
          // leading space
          skipChars(1);
          continue;
        }
        if (literalBufferedString.isNotEmpty()) {
          // there are accumulated chars
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), styleJsonObject);
          spacesCount = 0;
        }
        skipChars(1);
        literalBufferedString.clear();
        spacesCount++;
        continue;
      } else if (spacesCount > 0) {
        // literalStringBuffer must be null
        if (literalBufferedString.isNotEmpty()) {
          System.out.println("Accumulated chars at position " + inputPosition);
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), styleJsonObject);
        }
        literalBufferedString.clear();
        parentBlock = appendSpaceBlocks(parentBlock, spacesCount, styleJsonObject);
      }

      // not space char found

      spacesCount = 0;

      String encodedChar = parseEncodedChar();
      if (encodedChar != null) {
        literalBufferedString.appendString(encodedChar);
        continue;
      }
      if (peekString("&nbsp;")) {
        if (literalBufferedString.isNotEmpty()) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), styleJsonObject);
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_NON_BREAKABLE_SPACE, " ", styleJsonObject);
        skipChars(6);
        literalBufferedString.clear();
        continue;
      }

      literalBufferedString.appendChar(consumeChar());
    }

    if (literalBufferedString.isNotEmpty()) {
      // chars found
      JsonObject styleJsonObject;
      if (styleJsonObjects.isEmpty()) {
        styleJsonObject = null;
      } else {
        styleJsonObject = styleJsonObjects.get(styleJsonObjects.size() - 1);
      }
      appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), styleJsonObject);
    }
  }

  public boolean parseHtmlTextContentsStyle(ArrayList<JsonObject> styleJsonObjects) {
    // returns true if style stack changed or else returns false
    if (peekChar() != '<') {
      return false;
    }
    JsonObject styleJsonObject;
    if (styleJsonObjects.isEmpty()) {
      styleJsonObject = new JsonObject();
    } else {
      styleJsonObject = styleJsonObjects.get(styleJsonObjects.size() - 1);
      styleJsonObject = styleJsonObject.createCopy();
    }
    int storedPosition = inputPosition;
    skipChars(1);
    skipWhitespaces();
    if (peekChar() == '/') {
      skipChars(1);
      skipWhitespaces();
      String tagName = parseTagName();
      if (tagName.equals("strong")) {
      } else if (tagName.equals("b")) {
      } else if (tagName.equals("em")) {
      } else if (tagName.equals("i")) {
      } else if (tagName.equals("underline")) {
      } else if (tagName.equals("u")) {
      } else if (tagName.equals("del")) {
      } else if (tagName.equals("span")) {
      } else {
        inputPosition = storedPosition;
        return false;
      }
      skipWhitespaces();
      // >
      skipChars(1);
      if (styleJsonObjects.size() > 0) {
        styleJsonObjects.remove(styleJsonObjects.size() - 1);
      }
      return true;
    }
    String tagName = parseTagName();
    if (tagName.equals("strong")) {
      styleJsonObject.putStringValue("font-weight", "700");
      styleJsonObjects.add(styleJsonObject);
    } else if (tagName.equals("b")) {
      styleJsonObject.putStringValue("font-weight", "700");
      styleJsonObjects.add(styleJsonObject);
    } else if (tagName.equals("em")) {
      styleJsonObject.putStringValue("font-style", "italic");
      styleJsonObjects.add(styleJsonObject);
    } else if (tagName.equals("i")) {
      styleJsonObject.putStringValue("font-style", "italic");
      styleJsonObjects.add(styleJsonObject);
    } else if (tagName.equals("underline")) {
      styleJsonObject.putStringValue("text-decoration", "underline");
      styleJsonObjects.add(styleJsonObject);
    } else if (tagName.equals("u")) {
      styleJsonObject.putStringValue("text-decoration", "underline");
      styleJsonObjects.add(styleJsonObject);
    } else if (tagName.equals("del")) {
      styleJsonObject.putStringValue("text-decoration", "strike-through");
      styleJsonObjects.add(styleJsonObject);
      return true;
    } else if (tagName.equals("span")) {
      // start of style run
      Block block = new Block();
      block.styleJsonObject = styleJsonObject;
      parseHtmlAttributes(block);
    } else {
      inputPosition = storedPosition;
      return false;
    }
    skipWhitespaces();
    skipChars(1);
    return true;
  }

  private String parseEncodedChar() {
    if (peekString("&amp;")) {
      skipChars(5);
      return "&";
    }
    if (peekString("&lt;")) {
      skipChars(4);
      return "<";
    }
    if (peekString("&gt;")) {
      skipChars(4);
      return ">";
    }
    if (peekString("&quot;")) {
      skipChars(6);
      return "\"";
    }
    if (peekString("&#39;")) {
      skipChars(5);
      return "'";
    }
    if (peekString("&Aacute;")) {
      skipChars(8);
      return "Á";
    }
    if (peekString("&aacute;")) {
      skipChars(8);
      return "á";
    }
    if (peekString("&Eacute;")) {
      skipChars(8);
      return "É";
    }
    if (peekString("&eacute;")) {
      skipChars(8);
      return "é";
    }
    if (peekString("&Iacute;")) {
      skipChars(8);
      return "Í";
    }
    if (peekString("&iacute;")) {
      skipChars(8);
      return "í";
    }
    if (peekString("&Oacute;")) {
      skipChars(8);
      return "Ó";
    }
    if (peekString("&oacute;")) {
      skipChars(8);
      return "ó";
    }
    if (peekString("&Uacute;")) {
      skipChars(8);
      return "Ú";
    }
    if (peekString("&uacute;")) {
      skipChars(8);
      return "ú";
    }
    if (peekString("&Ntilde;")) {
      skipChars(8);
      return "Ñ";
    }
    if (peekString("&ntilde;")) {
      skipChars(8);
      return "ñ";
    }
    if (peekString("&copy;")) {
      skipChars(8);
      return "©";
    }
    if (peekString("&reg;")) {
      skipChars(5);
      return "®";
    }
    if (peekString("&trade;")) {
      skipChars(7);
      return "™";
    }
    if (peekString("&euro;")) {
      skipChars(6);
      return "€";
    }
    if (peekString("&pound;")) {
      skipChars(7);
      return "£";
    }
    if (peekString("&cent;")) {
      skipChars(6);
      return "¢";
    }
    if (peekString("&yen;")) {
      skipChars(5);
      return "¥";
    }
    if (peekString("&#")) {
      int storedPosition = inputPosition;

      skipChars(2);

      BufferedString bufferedString = new BufferedString();

      while (inputPosition < input.length()) {
        if (peekLineEnd()) {
          inputPosition = storedPosition;
          return null;
        }
        if (peekChar() == '<') {
          inputPosition = storedPosition;
          return null;
        }
        if (peekChar() == ';') {
          skipChars(1);
          break;
        }
        bufferedString.appendChar(consumeChar());
      }
      Integer integer = ExpectedRuntime.hexStringToInteger(bufferedString.getString());
      if (integer != null) {
        return String.valueOf((char) integer.intValue());
      } else {
        integer = ExpectedRuntime.stringToInteger(bufferedString.getString());
        if (integer != null) {
          return String.valueOf((char) integer.intValue());
        }
      }
      // wrong char, rewind
      inputPosition = storedPosition;
      return null;
    }
    return null;
  }

  public Block appendCharsBlock(Block parentBlock, String charsType, String chars, JsonObject styleJsonObject) {
    // <tag>#text</tag> convert to <tag><text>#text</text></tag>
    if (!(parentBlock instanceof TextBlock)) {
      TextBlock textBlock = new TextBlock();
      parentBlock.addBlock(textBlock);
      parentBlock = textBlock;
    }
    CharsBlock charsBlock = new CharsBlock();
    parentBlock.addBlock(charsBlock);
    charsBlock.type = charsType;
    charsBlock.setChars(chars);
    if (styleJsonObject != null) {
      charsBlock.styleJsonObject = styleJsonObject.createCopy();
    }
    return parentBlock;
  }

  public Block appendSpaceBlocks(Block parentBlock, int spacesCount, JsonObject styleJsonObject) {
    // <tag>#text</tag> convert to <tag><text>#text</text></tag>
    if (!(parentBlock instanceof TextBlock)) {
      TextBlock textBlock = new TextBlock();
      parentBlock.addBlock(textBlock);
      parentBlock = textBlock;
    }
    for (int i0 = 0; i0 < spacesCount; i0++) {
      CharsBlock charsBlock = new CharsBlock();
      parentBlock.addBlock(charsBlock);
      charsBlock.type = CharsBlock.TYPE_SPACE;
      charsBlock.setChars(" ");
      if (styleJsonObject != null) {
        charsBlock.styleJsonObject = styleJsonObject.createCopy();
      }
    }
    return parentBlock;
  }
}
