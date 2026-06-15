package franca.java.data.html;

import franca.java.expected.StringBuffer;
import franca.java.expected.Runtime;
import franca.java.data.Parser;
import franca.java.data.json.JsonArray;
import franca.java.data.json.JsonObject;
import franca.java.data.json.JsonStringPrimitive;
import franca.java.office.document.Block;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.TextBlock;

public class HtmlParser extends Parser {

  // true for HTML, false for MARKDOWN
  public boolean skipLeadingSpaces = true;

  public StringBuffer outputStringBuffer;
  private int outputSpacesNumber = 0;

  public Block parse(String input, StringBuffer outputStringBuffer) {
    this.input = input;
    this.outputStringBuffer = outputStringBuffer;

    position = 0;

    Block block = new Block();
    parseHtmlNodeContents(block);

    return block;
  }

  private void parseHtmlNodeContents(Block parentBlock) {

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

        CharsBlock charsBlock = new CharsBlock();
        parentBlock.addBlock(charsBlock);
        charsBlock.setChars(literalStringBuffer.getString());

        if (outputStringBuffer != null) {
          outputStringBuffer.appendChars('.', outputSpacesNumber);
          outputStringBuffer.appendString(literalStringBuffer.getString());
          outputStringBuffer.appendEndLine();
        }

        continue;
      }

      // can be part of inline element <span>, <strong>, <em>
      parseTextContents(parentBlock, new JsonArray());

      if (peekChar() != '<') {
        // corrupted html
        skipChars(1);
        return;
      }

      skipChars(1);

      if (parseClosingTag()) {
        return;
      }

      Block block = parseHtmlNode();
      if (block != null) {
        parentBlock.addBlock(block);
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
    Block block = createBlockByTagName(tagName);

    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString("< " + block.getClassName() + ", tagName = " + tagName);
      outputStringBuffer.appendEndLine();
    }

    JsonObject jsonObject = new JsonObject();
    block.attributesJsonArray.add(jsonObject);
    jsonObject.putStringValue("name", "tag-name");
    jsonObject.putStringValue("value", tagName);

    outputSpacesNumber += 2;
    parseHtmlAttributes(block);
    outputSpacesNumber -= 2;

    // self-closing tags

    if (isSelfClosingTag(tagName)) {
      if (outputStringBuffer != null) {
        outputStringBuffer.appendChars('.', outputSpacesNumber);
        outputStringBuffer.appendString("/>");
        outputStringBuffer.appendEndLine();
      }
      return block;
    }

    // '>'
    skipChars(1);

    outputSpacesNumber += 2;
    parseHtmlNodeContents(block);
    outputSpacesNumber -= 2;

    return block;
  }

  private String parseTagName() {
    StringBuffer stringBuffer = new StringBuffer();
    while ((position < input.length()) && (Character.isLetterOrDigit(peekChar()))) {
      char c = consumeChar();
      stringBuffer.appendChar(c);
    }
    return stringBuffer.getLowerCaseString();
  }

  public Block createBlockByTagName(String tagName) {
    if (tagName.equals("h1")) {
      return new HeadingBlock(1);
    }
    if (tagName.equals("h2")) {
      return new HeadingBlock(2);
    }
    if (tagName.equals("h3")) {
      return new HeadingBlock(3);
    }
    if (tagName.equals("h4")) {
      return new HeadingBlock(4);
    }
    if (tagName.equals("h5")) {
      return new HeadingBlock(5);
    }
    if (tagName.equals("h6")) {
      return new HeadingBlock(6);
    }
    if (tagName.equals("p")) {
      return new ParagraphBlock();
    }
    return new Block();
  }

  private boolean isSelfClosingTag(String tagName) {
    skipWhitespaces();

    if (peekChar() == '/') {
      skipChars(1);
      skipWhitespaces();
    }

    if (peekChar() != '>') {
      // corrupted html
      skipChars(1);
      return false;
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
    return true;
  }

  private boolean parseClosingTag() {

    skipWhitespaces();

    if (peekChar() != '/') {
      return false;
    }

    String closingTagName = parseTagName();

    skipWhitespaces();

    if (peekChar() != '>') {
      // corrupted html
      skipChars(1);
      return true;
    }

    skipChars(1);

    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString("</ " + closingTagName + " >");
      outputStringBuffer.appendEndLine();
    }
    return true;
  }

  private void parseHtmlAttributes(Block targetBlock) {

    while ((position < input.length()) && (peekChar() != '>') && (peekChar() != '/')) {
      skipWhitespaces();

      // attribute name

      String attributeName = parseAttributeName();
      if (attributeName == null) {
        return;
      }

      skipWhitespaces();

      if (peekChar() != '=') {
        targetBlock.attributesJsonArray.add(new JsonStringPrimitive(attributeName));
        if (outputStringBuffer != null) {
          outputStringBuffer.appendChars('.', outputSpacesNumber);
          outputStringBuffer.appendString(attributeName);
          outputStringBuffer.appendEndLine();
        }
        continue;
      }

      skipChars(1);

      if (attributeName.equals("class")) {
        parseClassAttribute(targetBlock.classesJsonArray);
      } else if (attributeName.equals("style")) {
        parseStyleAttribute(targetBlock.styleJsonArray);
      } else {
        parseAttributeValue(attributeName, targetBlock.attributesJsonArray);
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

      // style array contain pairs: styleName, styleValue, styleName, styleValue ...
      jsonArray.addStringValue(styleName);
      jsonArray.addStringValue(literalStringBuffer.getString());

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
      jsonObject.putStringValue("name", attributeName);
      jsonObject.putStringValue("value", literalStringBuffer.getString());

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
    JsonObject jsonObject = new JsonObject();
    jsonArray.add(jsonObject);
    jsonObject.putStringValue("name", attributeName);
    jsonObject.putStringValue("string-value", literalStringBuffer.getString());

    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString(attributeName + " = \"" + literalStringBuffer.getString() + "\"");
      outputStringBuffer.appendEndLine();
    }
  }

  public void parseTextContents(Block parentBlock, JsonArray stylesStackJsonArray) {

    literalStringBuffer = null;

    // for trailing spaces
    int spacesCount = -1;

    while (position < input.length()) {
      if (peekChar() == '\r') {
        skipChars(1);
        if (peekChar() == '\n') {
          skipChars(1);
        }
        // set spaces to leading
        spacesCount = -1;
        continue;
      }

      JsonArray styleJsonArray;
      if (stylesStackJsonArray.isEmpty()) {
        styleJsonArray = null;
      } else {
        styleJsonArray = stylesStackJsonArray.get(stylesStackJsonArray.size() - 1).asJsonArray();
      }

      if (parseStyleJsonArray(stylesStackJsonArray)) {
        if (spacesCount > 0) {
          // we accumulated spaces
          parentBlock = appendSpaceBlocks(parentBlock, spacesCount, styleJsonArray);
        } else if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styleJsonArray);
          literalStringBuffer = null;
        }
        spacesCount = 0;
        continue;
      }

      if (peekChar() == '<') {
        break;
      }

      if (peekChar() == ' ') {
        if (spacesCount < 0) {
          // leading space
          if (!skipLeadingSpaces) {
            spacesCount = 1;
          }
          skipChars(1);
          continue;
        }
        if (literalStringBuffer != null) {
          // there are accumulated chars
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styleJsonArray);
          literalStringBuffer = null;
          spacesCount = 0;
        }
        spacesCount++;
        continue;
      } else if (spacesCount > 0) {
        // literalStringBuffer must be null
        if (literalStringBuffer != null) {
          System.out.println("Accumulated chars at position " + position);
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styleJsonArray);
          literalStringBuffer = null;
        }
        parentBlock = appendSpaceBlocks(parentBlock, spacesCount, styleJsonArray);
      }

      // not space char found

      if (literalStringBuffer == null) {
        literalStringBuffer = new StringBuffer();
      }
      spacesCount = 0;

      char encodedChar = parseEncodedChar();
      if (encodedChar != (char) 0) {
        literalStringBuffer.appendChar(encodedChar);
        continue;
      }
      if (peekString("&nbsp;")) {
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styleJsonArray);
          literalStringBuffer = null;
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_NON_BREAKABLE_SPACE, " ", styleJsonArray);
        skipChars(6);
        continue;
      }
      if (peekString("<br>")) {
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styleJsonArray);
          literalStringBuffer = null;
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_LINE_BREAK, "", styleJsonArray);
        skipChars(4);
        continue;
      }

      literalStringBuffer.appendChar(consumeChar());
    }

    if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
      // chars found
      JsonArray styleJsonArray;
      if (stylesStackJsonArray.isEmpty()) {
        styleJsonArray = null;
      } else {
        styleJsonArray = stylesStackJsonArray.get(stylesStackJsonArray.size() - 1).asJsonArray();
      }
      appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styleJsonArray);
    }
  }

  public boolean parseStyleJsonArray(JsonArray stylesStackJsonArray) {
    // returns true if style stack changed or else returns false
    if (peekChar() != '<') {
      return false;
    }
    int storedPosition = position;
    skipChars(1);
    skipWhitespaces();
    if (peekString("span")) {
      // start of style run
      skipChars(5);
      Block block = new Block();
      parseHtmlAttributes(block);
      if (peekChar() == '>') {
        // ok span with style
        JsonArray styleJsonArray = new JsonArray();
        stylesStackJsonArray.add(styleJsonArray);
        for (int i = 0; i < block.styleJsonArray.size(); i++) {
          String string = block.styleJsonArray.get(i).getStringValue();
          if (string != null) {
            styleJsonArray.addStringValue(string);
          }
        }
        skipChars(1);
        return true;
      }
      // corrupted html
      return false;
    }
    if (peekChar() != '/') {
      // not span tag
      position = storedPosition;
      return false;
    }
    skipWhitespaces();
    if (!peekString("span")) {
      // not span tag
      position = storedPosition;
      return false;
    }
    skipChars(5);
    skipWhitespaces();
    // >
    skipChars(1);
    if (stylesStackJsonArray.isNotEmpty()) {
      stylesStackJsonArray.remove(stylesStackJsonArray.size() - 1);
    }
    return true;
  }

  private char parseEncodedChar() {
    if (peekString("&amp;")) {
      skipChars(5);
      return '&';
    }
    if (peekString("&lt;")) {
      skipChars(4);
      return '<';
    }
    if (peekString("&gt;")) {
      skipChars(4);
      return '>';
    }
    if (peekString("&quot;")) {
      skipChars(6);
      return '"';
    }
    if (peekString("&#39;")) {
      skipChars(5);
      return '\'';
    }
    if (peekString("&Aacute;")) {
      skipChars(8);
      return 'Á';
    }
    if (peekString("&aacute;")) {
      skipChars(8);
      return 'á';
    }
    if (peekString("&Eacute;")) {
      skipChars(8);
      return 'É';
    }
    if (peekString("&eacute;")) {
      skipChars(8);
      return 'é';
    }
    if (peekString("&Iacute;")) {
      skipChars(8);
      return 'Í';
    }
    if (peekString("&iacute;")) {
      skipChars(8);
      return 'í';
    }
    if (peekString("&Oacute;")) {
      skipChars(8);
      return 'Ó';
    }
    if (peekString("&oacute;")) {
      skipChars(8);
      return 'ó';
    }
    if (peekString("&Uacute;")) {
      skipChars(8);
      return 'Ú';
    }
    if (peekString("&uacute;")) {
      skipChars(8);
      return 'ú';
    }
    if (peekString("&Ntilde;")) {
      skipChars(8);
      return 'Ñ';
    }
    if (peekString("&ntilde;")) {
      skipChars(8);
      return 'ñ';
    }
    if (peekString("&copy;")) {
      skipChars(8);
      return '©';
    }
    if (peekString("&reg;")) {
      skipChars(5);
      return '®';
    }
    if (peekString("&trade;")) {
      skipChars(7);
      return '™';
    }
    if (peekString("&euro;")) {
      skipChars(6);
      return '€';
    }
    if (peekString("&pound;")) {
      skipChars(7);
      return '£';
    }
    if (peekString("&cent;")) {
      skipChars(6);
      return '¢';
    }
    if (peekString("&yen;")) {
      skipChars(5);
      return '¥';
    }
    if (peekString("&#")) {
      int storedPosition = position;

      skipChars(2);

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
        return (char) integer.intValue();
      } else {
        integer = Runtime.stringToInteger(stringBuffer.getString());
        if (integer != null) {
          return (char) integer.intValue();
        }
      }
      // wrong char, rewind
      position = storedPosition;
      return (char) 0;
    }
    return (char) 0;
  }

  private Block appendCharsBlock(Block parentBlock, String charsType, String chars, JsonArray styleJsonArray) {
    // <tag>#text</tag> convert to <tag><text>#text</text></tag>
    if (!(parentBlock instanceof TextBlock)) {
      TextBlock textBlock = new TextBlock();
      parentBlock.addBlock(textBlock);
      parentBlock = textBlock;
      if (outputStringBuffer != null) {
        outputStringBuffer.appendChars('.', outputSpacesNumber);
        outputStringBuffer.appendString("< " + parentBlock.getClassName() + " >");
        outputStringBuffer.appendEndLine();
      }
    }
    CharsBlock charsBlock = new CharsBlock();
    parentBlock.addBlock(charsBlock);
    charsBlock.type = charsType;
    charsBlock.setChars(chars);
    // styleArray contain pairs: styleName, styleValue, styleName, styleValue ...
    for (int i = 0; i < styleJsonArray.size(); i++) {
      String string = styleJsonArray.get(i).getStringValue();
      if (string != null) {
        charsBlock.styleJsonArray.addStringValue(string);
      }
    }
    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString(charsType + " \"" + chars + "\"");
      outputStringBuffer.appendEndLine();
    }
    return parentBlock;
  }

  private Block appendSpaceBlocks(Block parentBlock, int spacesCount, JsonArray styleJsonArray) {
    // <tag>#text</tag> convert to <tag><text>#text</text></tag>
    if (!(parentBlock instanceof TextBlock)) {
      TextBlock textBlock = new TextBlock();
      parentBlock.addBlock(textBlock);
      parentBlock = textBlock;
      if (outputStringBuffer != null) {
        outputStringBuffer.appendChars('.', outputSpacesNumber);
        outputStringBuffer.appendString("< " + parentBlock.getClassName() + " >");
        outputStringBuffer.appendEndLine();
      }
    }
    for (int i0 = 0; i0 < spacesCount; i0++) {
      CharsBlock charsBlock = new CharsBlock();
      parentBlock.addBlock(charsBlock);
      charsBlock.type = CharsBlock.TYPE_SPACE;
      charsBlock.setChars(" ");
      if (styleJsonArray != null) {
        // styleArray contain pairs: styleName, styleValue, styleName, styleValue ...
        for (int i1 = 0; i1 < styleJsonArray.size(); i1++) {
          String string = styleJsonArray.get(i1).getStringValue();
          if (string != null) {
            charsBlock.styleJsonArray.addStringValue(string);
          }
        }
      }
      if (outputStringBuffer != null) {
        outputStringBuffer.appendChars('.', outputSpacesNumber);
        outputStringBuffer.appendString("space" + " \" \"");
        outputStringBuffer.appendEndLine();
      }
    }
    return parentBlock;
  }
}
