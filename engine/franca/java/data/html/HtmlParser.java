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
    parseHtmlNodeContents(null, block);

    return block;
  }

  private void parseHtmlNodeContents(String tagName, Block parentBlock) {

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
      parseTextContents(parentBlock);

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
            outputStringBuffer.appendString("</ " + parentBlock.getClassName() + " >");
            outputStringBuffer.appendEndLine();
          }
          return;
        }

        if (closingTagName.equals(tagName)) {
          if (outputStringBuffer != null) {
            outputSpacesNumber -= 2;
            outputStringBuffer.appendChars('.', outputSpacesNumber);
            outputStringBuffer.appendString("</ " + parentBlock.getClassName() + " >");
            outputStringBuffer.appendEndLine();
          }
          return;
        }
      }

      skipChars(1);

      String childTagName = parseTagName();
      Block childBlock;
      if (childTagName.equals("h1")) {
        childBlock = new HeadingBlock(1);
      } else if (childTagName.equals("h2")) {
        childBlock = new HeadingBlock(2);
      } else if (childTagName.equals("h3")) {
        childBlock = new HeadingBlock(3);
      } else if (childTagName.equals("h4")) {
        childBlock = new HeadingBlock(4);
      } else if (childTagName.equals("h5")) {
        childBlock = new HeadingBlock(5);
      } else if (childTagName.equals("h6")) {
        childBlock = new HeadingBlock(6);
      } else if (childTagName.equals("p")) {
        childBlock = new ParagraphBlock();
      } else {
        childBlock = new Block();
      }

      parentBlock.addBlock(childBlock);

      int storedPosition = position;
      parseHtmlNode(childTagName, childBlock);
      if (storedPosition == position) {
        // avoid cycling
        return;
      }
    }
  }

  public void parseHtmlNode(String tagName, Block targetBlock) {

    skipWhitespaces();

    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString("< " + targetBlock.getClassName() + ", tagName = " + tagName);
      outputStringBuffer.appendEndLine();
    }

    JsonObject jsonObject = new JsonObject();
    targetBlock.attributes.add(jsonObject);
    jsonObject.putStringValue("name", "tag-name");
    jsonObject.putStringValue("value", tagName);

    outputSpacesNumber += 2;
    parseHtmlAttributes(targetBlock);
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

    outputSpacesNumber += 2;
    parseHtmlNodeContents(tagName, targetBlock);
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
        targetBlock.attributes.add(new JsonStringPrimitive(attributeName));
        if (outputStringBuffer != null) {
          outputStringBuffer.appendChars('.', outputSpacesNumber);
          outputStringBuffer.appendString(attributeName);
          outputStringBuffer.appendEndLine();
        }
        continue;
      }

      skipChars(1);

      if (attributeName.equals("class")) {
        parseClassAttribute(targetBlock.classes);
      } else if (attributeName.equals("style")) {
        parseStyleAttribute(targetBlock.style);
      } else {
        parseAttributeValue(attributeName, targetBlock.attributes);
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

  public void parseTextContents(Block parentBlock) {

    literalStringBuffer = null;
    JsonArray styles = new JsonArray();

    // for trailing spaces
    int spacesCount = -1;

    while (position < input.length()) {
      if (peekChar() == '<') {
        break;
      }

      if (peekChar() == '\r') {
        skipChars(1);
        if (peekChar() == '\n') {
          skipChars(1);
        }
        // set spaces to leading
        spacesCount = -1;
        continue;
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
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styles);
          literalStringBuffer = null;
          spacesCount = 0;
        }
        spacesCount++;
        continue;
      } else if (spacesCount > 0) {
        // literalStringBuffer must be null
        if (literalStringBuffer != null) {
          System.out.println("Accumulated chars at position " + position);
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styles);
          literalStringBuffer = null;
        }
        parentBlock = appendSpaceBlocks(parentBlock, spacesCount, styles);
      }

      // not space char found

      if (parseStyle(styles)) {
        // format found
      }

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
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styles);
          literalStringBuffer = null;
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_NON_BREAKABLE_SPACE, " ", styles);
        skipChars(6);
        continue;
      }
      if (peekString("<br>")) {
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styles);
          literalStringBuffer = null;
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_LINE_BREAK, "", styles);
        skipChars(4);
        continue;
      }

      literalStringBuffer.appendChar(consumeChar());
    }

    if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
      // chars found
      appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), styles);
    }
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

  private Block appendCharsBlock(Block parentBlock, String charsType, String chars, JsonArray styles) {
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
    if (styles.isNotEmpty()) {
      JsonArray styleArray = styles.get(styles.size() - 1).asJsonArray();
      // styleArray contain pairs: styleName, styleValue, styleName, styleValue ...
      for (int i = 0; i < styleArray.size(); i++) {
        String string = styleArray.get(i).getStringValue();
        if (string != null) {
          charsBlock.style.addStringValue(string);
        }
      }
    }
    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString(charsType + " \"" + chars + "\"");
      outputStringBuffer.appendEndLine();
    }
    return parentBlock;
  }

  private Block appendSpaceBlocks(Block parentBlock, int spacesCount, JsonArray styles) {
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
    JsonArray styleArray;
    if (styles.isEmpty()) {
      styleArray = null;
    } else {
      styleArray = styles.get(styles.size() - 1).asJsonArray();
    }
    for (int i0 = 0; i0 < spacesCount; i0++) {
      CharsBlock charsBlock = new CharsBlock();
      parentBlock.addBlock(charsBlock);
      charsBlock.type = CharsBlock.TYPE_SPACE;
      charsBlock.setChars(" ");
      if (styleArray != null) {
        // styleArray contain pairs: styleName, styleValue, styleName, styleValue ...
        for (int i1 = 0; i1 < styleArray.size(); i1++) {
          String string = styleArray.get(i1).getStringValue();
          if (string != null) {
            charsBlock.style.addStringValue(string);
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
