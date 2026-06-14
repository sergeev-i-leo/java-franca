package franca.java.data.html;

import franca.java.expected.StringBuffer;
import franca.java.expected.Runtime;
import franca.java.data.Parser;
import franca.java.data.json.JsonArray;
import franca.java.data.json.JsonObject;
import franca.java.data.json.JsonStringPrimitive;
import franca.java.office.document.Block;
import franca.java.office.document.typography_blocks.HeadingBlock;
import franca.java.office.document.typography_blocks.ParagraphBlock;
import franca.java.office.document.typography_blocks.LettersBlock;
import franca.java.office.document.typography_blocks.TextBlock;

public class HtmlParser extends Parser {

  // 0 for nothing, 1 for nothing with input flag, 2 for debugging
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

        LettersBlock lettersBlock = new LettersBlock();
        parentBlock.addBlock(lettersBlock);
        lettersBlock.setText(literalStringBuffer.getString());

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

      skipChars(1 + childTagName.length());

      int storedPosition = position;
      parseHtmlNode(childTagName, childBlock);
      if (storedPosition == position) {
        // avoid cycling
        return;
      }
    }
  }

  public void parseHtmlNode(String tagName, Block targetBlock) {

    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString("< " + targetBlock.getClassName());
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

  private void parseTextContents(Block parentBlock) {

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
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendTextBlock(parentBlock, LettersBlock.TYPE_TEXT, literalStringBuffer.getString());
          literalStringBuffer = null;
        }
        parentBlock = appendTextBlock(parentBlock, LettersBlock.TYPE_NON_BREAKABLE_SPACE, " ");
        skipSpaces = false;
        skipChars(6);
        continue;
      }
      if (peekString("<br>")) {
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendTextBlock(parentBlock, LettersBlock.TYPE_TEXT, literalStringBuffer.getString());
          literalStringBuffer = null;
        }
        parentBlock = appendTextBlock(parentBlock, LettersBlock.TYPE_LINE_BREAK, "");
        skipSpaces = true;
        skipChars(4);
        continue;
      }
      if (peekChar() == ' ') {
        if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
          parentBlock = appendTextBlock(parentBlock, LettersBlock.TYPE_TEXT, literalStringBuffer.getString());
          literalStringBuffer = null;
        }
        parentBlock = appendTextBlock(parentBlock, LettersBlock.TYPE_SPACE, " ");
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

    if ((literalStringBuffer != null) && (literalStringBuffer.isNotEmpty())) {
      // text found
      appendTextBlock(parentBlock, LettersBlock.TYPE_TEXT, literalStringBuffer.getString());
    }
  }

  private Block appendTextBlock(Block parentBlock, String textType, String text) {
    // <tag>#text</tag> convert to <tag><text>#text</text></tag>
    if (!(parentBlock instanceof TextBlock)) {
      TextBlock textBlock = new TextBlock();
      parentBlock.addBlock(textBlock);
      parentBlock = textBlock;
    }
    LettersBlock lettersBlock = new LettersBlock();
    parentBlock.addBlock(lettersBlock);
    lettersBlock.type = textType;
    lettersBlock.setText(text);
    if (outputStringBuffer != null) {
      outputStringBuffer.appendChars('.', outputSpacesNumber);
      outputStringBuffer.appendString(textType + " \"" + text + "\"");
      outputStringBuffer.appendEndLine();
    }
    return parentBlock;
  }
}
