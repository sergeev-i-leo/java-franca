package franca.java.data.html;

import java.util.ArrayList;

import franca.java.expected.StringBuffer;
import franca.java.expected.ExpectedRuntime;
import franca.java.data.Parser;
import franca.java.data.json.JsonArray;
import franca.java.data.json.JsonObject;
import franca.java.data.json.JsonStringPrimitive;
import franca.java.office.document.Block;
import franca.java.office.document.BlockStyle;
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
      ArrayList<BlockStyle> blockStyles = new ArrayList<>();
      parseTextContents(parentBlock, blockStyles);

      if (peekChar() != '<') {
        // corrupted html
        skipChars(1);
        return;
      }

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

    skipChars(1);

    return true;
  }

  private boolean parseClosingTag() {
    if (peekChar() != '<') {
      // not a node tag is missing
      return false;
    }

    int storedPosition = position;

    skipChars(1);

    skipWhitespaces();

    if (peekChar() != '/') {
      position = storedPosition;
      return false;
    }

    skipChars(1);

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
        parseStyleAttribute(targetBlock.blockStyle);
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

  private void parseStyleAttribute(BlockStyle blockStyle) {
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

      String styleValue = literalStringBuffer.getString();
      switch (styleName) {
        case "color":
          blockStyle.color = styleValue;
          break;
        case "background-color":
          blockStyle.backgroundColor = styleValue;
          break;
        case "text-align":
          blockStyle.textAlign = styleValue;
          break;
        case "font-weight":
          blockStyle.fontWeight = styleValue;
          break;
        case "font-style":
          if (styleValue.equals("italic")) {
            blockStyle.isItalic = true;
          } else if (styleValue.equals("normal")) {
            // reset font
            blockStyle.isItalic = false;
          }
          break;
        case "text-decoration":
          if (styleValue.equals("none")) {
            blockStyle.isUnderline = false;
            blockStyle.isStrikethrough = false;
          } else {
            if (styleValue.contains("underline")) {
              blockStyle.isUnderline = true;
            }
            if (styleValue.contains("strike-through")) {
              blockStyle.isStrikethrough = true;
            }
          }
          break;
      }

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

  public void parseTextContents(Block parentBlock, ArrayList<BlockStyle> blockStyles) {

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

      BlockStyle blockStyle;
      if (blockStyles.isEmpty()) {
        blockStyle = null;
      } else {
        blockStyle = blockStyles.get(blockStyles.size() - 1);
      }

      if (parseBlockStyle(blockStyles)) {
        if (spacesCount > 0) {
          // we accumulated spaces
          parentBlock = appendSpaceBlocks(parentBlock, spacesCount, blockStyle);
        } else if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), blockStyle);
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
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), blockStyle);
          literalStringBuffer = null;
          spacesCount = 0;
        }
        skipChars(1);
        spacesCount++;
        continue;
      } else if (spacesCount > 0) {
        // literalStringBuffer must be null
        if (literalStringBuffer != null) {
          System.out.println("Accumulated chars at position " + position);
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), blockStyle);
          literalStringBuffer = null;
        }
        parentBlock = appendSpaceBlocks(parentBlock, spacesCount, blockStyle);
      }

      // not space char found

      if (literalStringBuffer == null) {
        literalStringBuffer = new StringBuffer();
      }
      spacesCount = 0;

      String encodedChar = parseEncodedChar();
      if (encodedChar != null) {
        literalStringBuffer.appendString(encodedChar);
        continue;
      }
      if (peekString("&nbsp;")) {
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), blockStyle);
          literalStringBuffer = null;
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_NON_BREAKABLE_SPACE, " ", blockStyle);
        skipChars(6);
        continue;
      }
      if (peekString("<br>")) {
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), blockStyle);
          literalStringBuffer = null;
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_LINE_BREAK, "", blockStyle);
        skipChars(4);
        continue;
      }

      literalStringBuffer.appendChar(consumeChar());
    }

    if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
      // chars found
      BlockStyle blockStyle;
      if (blockStyles.isEmpty()) {
        blockStyle = null;
      } else {
        blockStyle = blockStyles.get(blockStyles.size() - 1);
      }
      appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalStringBuffer.getString(), blockStyle);
    }
  }

  public boolean parseBlockStyle(ArrayList<BlockStyle> blockStyles) {
    // returns true if style stack changed or else returns false
    if (peekChar() != '<') {
      return false;
    }
    BlockStyle blockStyle;
    if (blockStyles.isEmpty()) {
      blockStyle = new BlockStyle();
    } else {
      blockStyle = blockStyles.get(blockStyles.size() - 1);
      blockStyle = blockStyle.clone();
    }
    int storedPosition = position;
    skipChars(1);
    skipWhitespaces();
    if (peekString("strong")) {
      skipChars(6);
      blockStyle.fontWeight = "700";
      blockStyles.add(blockStyle);
      skipWhitespaces();
      skipChars(1);
      return true;
    }
    if (peekString("b")) {
      skipChars(1);
      blockStyle.fontWeight = "700";
      blockStyles.add(blockStyle);
      skipWhitespaces();
      skipChars(1);
      return true;
    }
    if (peekString("em")) {
      skipChars(2);
      blockStyle.isItalic = true;
      blockStyles.add(blockStyle);
      skipWhitespaces();
      skipChars(1);
      return true;
    }
    if (peekString("i")) {
      skipChars(1);
      blockStyle.isItalic = true;
      blockStyles.add(blockStyle);
      skipWhitespaces();
      skipChars(1);
      return true;
    }
    if (peekString("underline")) {
      skipChars(9);
      blockStyle.isUnderline = true;
      blockStyles.add(blockStyle);
      skipWhitespaces();
      skipChars(1);
      return true;
    }
    if (peekString("u")) {
      skipChars(1);
      blockStyle.isUnderline = true;
      blockStyles.add(blockStyle);
      skipWhitespaces();
      skipChars(1);
      return true;
    }
    if (peekString("del")) {
      skipChars(9);
      blockStyle.isUnderline = true;
      blockStyles.add(blockStyle);
      skipWhitespaces();
      skipChars(1);
      return true;
    }
    if (peekString("span")) {
      // start of style run
      skipChars(5);
      Block block = new Block();
      block.blockStyle = blockStyle;
      parseHtmlAttributes(block);
      if (peekChar() == '>') {
        // ok span with style
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
    if (peekString("strong")) {
      skipChars(6);
    } else if (peekString("b")) {
      skipChars(1);
    } else if (peekString("em")) {
      skipChars(2);
    } else if (peekString("i")) {
      skipChars(1);
    } else if (peekString("underline")) {
      skipChars(9);
    } else if (peekString("u")) {
      skipChars(1);
    } else if (peekString("del")) {
      skipChars(3);
    } else if (peekString("span")) {
      skipChars(4);
    } else {
      position = storedPosition;
      return false;
    }
    skipWhitespaces();
    // >
    skipChars(1);
    if (blockStyles.size() > 0) {
      blockStyles.remove(blockStyles.size() - 1);
    }
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
      Integer integer = ExpectedRuntime.hexStringToInteger(stringBuffer.getString());
      if (integer != null) {
        return String.valueOf((char) integer.intValue());
      } else {
        integer = ExpectedRuntime.stringToInteger(stringBuffer.getString());
        if (integer != null) {
          return String.valueOf((char) integer.intValue());
        }
      }
      // wrong char, rewind
      position = storedPosition;
      return null;
    }
    return null;
  }

  private Block appendCharsBlock(Block parentBlock, String charsType, String chars, BlockStyle blockStyle) {
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
    if (blockStyle != null) {
      charsBlock.blockStyle = blockStyle.clone();
    }
    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString(charsType + " \"" + chars + "\"");
      outputStringBuffer.appendEndLine();
      System.out.println(charsType + " \"" + chars + "\"");
    }
    return parentBlock;
  }

  private Block appendSpaceBlocks(Block parentBlock, int spacesCount, BlockStyle blockStyle) {
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
      if (blockStyle != null) {
        charsBlock.blockStyle = blockStyle.clone();
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
