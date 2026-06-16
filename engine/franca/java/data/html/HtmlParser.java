package franca.java.data.html;

import java.util.ArrayList;

import franca.java.expected.BufferedString;
import franca.java.expected.ExpectedRuntime;
import franca.java.data.Parser;
import franca.java.data.json.JsonArray;
import franca.java.data.json.JsonObject;
import franca.java.data.json.JsonStringPrimitive;
import franca.java.office.document.Block;
import franca.java.office.document.BlockStyle;
import franca.java.office.document.list.ListBlock;
import franca.java.office.document.list.ListItemBlock;
import franca.java.office.document.structure.HorizontalRuleBlock;
import franca.java.office.document.table.*;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.TextBlock;

public class HtmlParser extends Parser {

  // true for HTML, false for MARKDOWN
  public boolean skipLeadingSpaces = true;

  public BufferedString outputBufferedString;
  private int outputSpacesNumber = 0;

  public Block parse(String input, BufferedString outputBufferedString) {
    this.input = input;
    this.outputBufferedString = outputBufferedString;

    inputPosition = 0;

    Block block = new Block();
    parseHtmlNodeContents(block);

    return block;
  }

  private void parseHtmlNodeContents(Block parentBlock) {

    while (inputPosition < input.length()) {
      skipWhitespaces();

      if ((peekChar() == '<') && (peekNextChar(1) == '!')) {
        literalBufferedString = new BufferedString();
        literalBufferedString.appendString("<!");
        skipChars(2);
        while (inputPosition < input.length()) {
          // html comment mustn't contain >
          char c = consumeChar();
          if (c == '>') {
            literalBufferedString.appendChar(c);
            break;
          }
          literalBufferedString.appendChar(c);
        }

        CharsBlock charsBlock = new CharsBlock();
        parentBlock.addBlock(charsBlock);
        charsBlock.setChars(literalBufferedString.getString());

        if (outputBufferedString != null) {
          outputBufferedString.appendChars('.', outputSpacesNumber);
          outputBufferedString.appendString(literalBufferedString.getString());
          outputBufferedString.appendEndLine();
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
    Block block = createBlockByTagName(tagName);

    if (outputBufferedString != null) {
      outputBufferedString.appendChars('.', outputSpacesNumber);
      outputBufferedString.appendString("< " + block.getClassName() + ", tagName = " + tagName);
      outputBufferedString.appendEndLine();
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
      if (outputBufferedString != null) {
        outputBufferedString.appendChars('.', outputSpacesNumber);
        outputBufferedString.appendString("/>");
        outputBufferedString.appendEndLine();
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
    BufferedString bufferedString = new BufferedString();
    while ((inputPosition < input.length()) && (Character.isLetterOrDigit(peekChar()))) {
      char c = consumeChar();
      bufferedString.appendChar(c);
    }
    return bufferedString.getLowerCaseString();
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
    if (tagName.equals("hr")) {
      return new HorizontalRuleBlock();
    }
    if (tagName.equals("ul")) {
      return new ListBlock(false);
    }
    if (tagName.equals("ol")) {
      return new ListBlock(true);
    }
    if (tagName.equals("li")) {
      return new ListItemBlock();
    }
    if (tagName.equals("table")) {
      return new TableBlock();
    }
    if (tagName.equals("thead")) {
      return new TableHeaderBlock();
    }
    if (tagName.equals("tbody")) {
      return new TableBodyBlock();
    }
    if (tagName.equals("tr")) {
      return new TableRowBlock();
    }
    if (tagName.equals("th")) {
      return new TableCellBlock(true);
    }
    if (tagName.equals("td")) {
      return new TableCellBlock(false);
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

    int storedPosition = inputPosition;

    skipChars(1);

    skipWhitespaces();

    if (peekChar() != '/') {
      inputPosition = storedPosition;
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

    if (outputBufferedString != null) {
      outputBufferedString.appendChars('.', outputSpacesNumber);
      outputBufferedString.appendString("</ " + closingTagName + " >");
      outputBufferedString.appendEndLine();
    }
    return true;
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
        if (outputBufferedString != null) {
          outputBufferedString.appendChars('.', outputSpacesNumber);
          outputBufferedString.appendString(attributeName);
          outputBufferedString.appendEndLine();
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
    BufferedString bufferedString = new BufferedString();
    while ((inputPosition < input.length()) && (isAttributeNameCharacter(peekChar()))) {
      char c = consumeChar();
      bufferedString.appendChar(c);
    }
    if (bufferedString.isEmpty()) {
      return null;
    }

    return bufferedString.getLowerCaseString();
  }

  private boolean isAttributeNameCharacter(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void parseClassAttribute(JsonArray jsonArray) {
    skipWhitespaces();

    literalBufferedString = new BufferedString();

    char classValueDelimiter = peekChar();
    skipChars(1);

    while (inputPosition < input.length()) {
      if (peekChar() == classValueDelimiter) {
        if (literalBufferedString.isNotEmpty()) {
          jsonArray.add(new JsonStringPrimitive(literalBufferedString.getLowerCaseString()));
          if (outputBufferedString != null) {
            outputBufferedString.appendChars('.', outputSpacesNumber);
            outputBufferedString.appendString("class." + literalBufferedString.getString());
            outputBufferedString.appendEndLine();
          }
        }
        break;
      }
      if (peekChar() == ' ') {
        if (literalBufferedString.isNotEmpty()) {
          jsonArray.add(new JsonStringPrimitive(literalBufferedString.getLowerCaseString()));
          if (outputBufferedString != null) {
            outputBufferedString.appendChars('.', outputSpacesNumber);
            outputBufferedString.appendString("class." + literalBufferedString.getString());
            outputBufferedString.appendEndLine();
          }
        }
        literalBufferedString = new BufferedString();
        skipChars(1);
      } else {
        literalBufferedString.appendChar(consumeChar());
      }
    }

    skipChars(1);
  }

  private void parseStyleAttribute(BlockStyle blockStyle) {
    skipWhitespaces();

    char styleValueDelimiter = peekChar();
    skipChars(1);

    while (inputPosition < input.length()) {
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
      if (literalBufferedString.isEmpty()) {
        break;
      }

      String styleValue = literalBufferedString.getString();
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

      if (outputBufferedString != null) {
        outputBufferedString.appendChars('.', outputSpacesNumber);
        outputBufferedString.appendString("style." + styleName + " : " + literalBufferedString.getString());
        outputBufferedString.appendEndLine();
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

      if (outputBufferedString != null) {
        outputBufferedString.appendChars('.', outputSpacesNumber);
        outputBufferedString.appendString(attributeName + " = " + literalBufferedString.getString());
        outputBufferedString.appendEndLine();
      }

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
    jsonObject.putStringValue("string-value", literalBufferedString.getString());

    if (outputBufferedString != null) {
      outputBufferedString.appendChars('.', outputSpacesNumber);
      outputBufferedString.appendString(attributeName + " = \"" + literalBufferedString.getString() + "\"");
      outputBufferedString.appendEndLine();
    }
  }

  public void parseTextContents(Block parentBlock, ArrayList<BlockStyle> blockStyles) {

    literalBufferedString = new BufferedString();

    // for trailing spaces
    int spacesCount;
    if (skipLeadingSpaces) {
      spacesCount = -1;
    } else {
      spacesCount = 0;
    }

    while (inputPosition < input.length()) {
      if (peekLineEnd()) {
        skipLineEnd();
        // set spaces to leading
        if (skipLeadingSpaces) {
          spacesCount = -1;
        } else {
          spacesCount = 0;
        }
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
        } else if (literalBufferedString.isNotEmpty()) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
        }
        literalBufferedString.clear();
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
        if (literalBufferedString.isNotEmpty()) {
          // there are accumulated chars
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
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
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
        }
        literalBufferedString.clear();
        parentBlock = appendSpaceBlocks(parentBlock, spacesCount, blockStyle);
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
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_NON_BREAKABLE_SPACE, " ", blockStyle);
        skipChars(6);
        literalBufferedString.clear();
        continue;
      }
      if (peekString("<br>")) {
        if (literalBufferedString.isNotEmpty()) {
          parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
        }
        parentBlock = appendCharsBlock(parentBlock, CharsBlock.TYPE_LINE_BREAK, "", blockStyle);
        skipChars(4);
        literalBufferedString.clear();
        continue;
      }

      literalBufferedString.appendChar(consumeChar());
    }

    if (literalBufferedString.isNotEmpty()) {
      // chars found
      BlockStyle blockStyle;
      if (blockStyles.isEmpty()) {
        blockStyle = null;
      } else {
        blockStyle = blockStyles.get(blockStyles.size() - 1);
      }
      appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
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
      if (blockStyles.size() > 0) {
        blockStyles.remove(blockStyles.size() - 1);
      }
      if (outputBufferedString != null) {
        outputBufferedString.appendChars('.', outputSpacesNumber);
        outputBufferedString.appendString("</" + tagName + ">");
        outputBufferedString.appendEndLine();
      }
      return true;
    }
    String tagName = parseTagName();
    if (tagName.equals("strong")) {
      blockStyle.fontWeight = "700";
      blockStyles.add(blockStyle);
    } else if (tagName.equals("b")) {
      blockStyle.fontWeight = "700";
      blockStyles.add(blockStyle);
    } else if (tagName.equals("em")) {
      blockStyle.isItalic = true;
      blockStyles.add(blockStyle);
    } else if (tagName.equals("i")) {
      blockStyle.isItalic = true;
      blockStyles.add(blockStyle);
    } else if (tagName.equals("underline")) {
      blockStyle.isUnderline = true;
      blockStyles.add(blockStyle);
    } else if (tagName.equals("u")) {
      blockStyle.isUnderline = true;
      blockStyles.add(blockStyle);
    } else if (tagName.equals("del")) {
      blockStyle.isUnderline = true;
      blockStyles.add(blockStyle);
      return true;
    } else if (tagName.equals("span")) {
      // start of style run
      Block block = new Block();
      block.blockStyle = blockStyle;
      parseHtmlAttributes(block);
    } else {
      inputPosition = storedPosition;
      return false;
    }
    skipWhitespaces();
    skipChars(1);
    if (outputBufferedString != null) {
      outputBufferedString.appendChars('.', outputSpacesNumber);
      outputBufferedString.appendString("<" + tagName + ">");
      outputBufferedString.appendEndLine();
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
      int storedPosition = inputPosition;

      skipChars(2);

      BufferedString bufferedString = new BufferedString();

      while (inputPosition < input.length()) {
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

  private Block appendCharsBlock(Block parentBlock, String charsType, String chars, BlockStyle blockStyle) {
    // <tag>#text</tag> convert to <tag><text>#text</text></tag>
    if (!(parentBlock instanceof TextBlock)) {
      TextBlock textBlock = new TextBlock();
      parentBlock.addBlock(textBlock);
      parentBlock = textBlock;
      if (outputBufferedString != null) {
        outputBufferedString.appendChars('.', outputSpacesNumber);
        outputBufferedString.appendString("< " + parentBlock.getClassName() + " >");
        outputBufferedString.appendEndLine();
      }
    }
    CharsBlock charsBlock = new CharsBlock();
    parentBlock.addBlock(charsBlock);
    charsBlock.type = charsType;
    charsBlock.setChars(chars);
    if (blockStyle != null) {
      charsBlock.blockStyle = blockStyle.clone();
    }
    if (outputBufferedString != null) {
      outputBufferedString.appendChars('.', outputSpacesNumber);
      outputBufferedString.appendString(charsType + " \"" + chars + "\"");
      outputBufferedString.appendEndLine();
    }
    return parentBlock;
  }

  private Block appendSpaceBlocks(Block parentBlock, int spacesCount, BlockStyle blockStyle) {
    // <tag>#text</tag> convert to <tag><text>#text</text></tag>
    if (!(parentBlock instanceof TextBlock)) {
      TextBlock textBlock = new TextBlock();
      parentBlock.addBlock(textBlock);
      parentBlock = textBlock;
      if (outputBufferedString != null) {
        outputBufferedString.appendChars('.', outputSpacesNumber);
        outputBufferedString.appendString("< " + parentBlock.getClassName() + " >");
        outputBufferedString.appendEndLine();
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
      if (outputBufferedString != null) {
        outputBufferedString.appendChars('.', outputSpacesNumber);
        outputBufferedString.appendString("space" + " \" \"");
        outputBufferedString.appendEndLine();
      }
    }
    return parentBlock;
  }
}
