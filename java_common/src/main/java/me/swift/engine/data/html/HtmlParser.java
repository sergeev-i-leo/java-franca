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

      while ((position < input.length()) && (peekCharacter() != '<')) {
        consumeCharacter();
      }

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
    JsonObject jsonObject = null;
    if (tagName.equals("img")) {
      jsonObject = new JsonObject();
      viewsJsonArray.addElement(jsonObject);
      jsonObject.setStringMember("tagName", tagName);
      jsonObject.setStringMember("className", "image-view");
      parseHtmlAttributes(jsonObject);
      // self-closing
      consumeCharacter();
      return;
    }
    if (tagName.equals("tbody")) {
      parseHtmlNodeContents(tagName, viewsJsonArray);
      return;
    }
    if (tagName.equals("span")) {
      parseHtmlNodeContents(tagName, viewsJsonArray);
      return;
    }
    jsonObject = new JsonObject();
    switch (tagName) {
      case "h1":
        jsonObject.setStringMember("className", "typography-h1-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "h2":
        jsonObject.setStringMember("className", "typography-h2-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "h3":
        jsonObject.setStringMember("className", "typography-h3-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "h4":
        jsonObject.setStringMember("className", "typography-h4-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "h5":
        jsonObject.setStringMember("className", "typography-h5-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "h6":
        jsonObject.setStringMember("className", "typography-h6-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "p":
        jsonObject.setStringMember("className", "typography-paragraph-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "table":
        jsonObject.setStringMember("className", "table-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "tr":
        jsonObject.setStringMember("className", "table-row-view");
        parseHtmlAttributes(jsonObject);
        break;
      case "th":
        parseHtmlAttributes(jsonObject);
        stuffCellJsonObject(jsonObject, "table-header-cell-view");
        break;
      case "td":
        parseHtmlAttributes(jsonObject);
        stuffCellJsonObject(jsonObject, "table-cell-view");
        break;
      default:
        break;
    }

    viewsJsonArray.addElement(jsonObject);
    jsonObject.setStringMember("tagName", tagName);
    if ((peekCharacter() == '/') && (peekNextCharacter(1) == '>')) {
      // self-closing
      consumeCharacter();
      consumeCharacter();
      return;
    }

    // '>'
    consumeCharacter();

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
    JsonObject jsonObject = parseTextContents();

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

        char quote = peekCharacter();
        if ((quote == '"') || (quote == '\'')) {
          // skip quote
          consumeCharacter();

          while ((position < input.length()) && (peekCharacter() != quote)) {
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
      }
    }
  }

  private boolean isAttributeNameChar(char c) {
    return (c != '=') && (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private boolean isAttributeValueChar(char c) {
    return (c != '>') && (c != '/') && (!Character.isWhitespace(c));
  }

  private void stuffCellJsonObject(JsonObject cellJsonObject, String className) {
    cellJsonObject.setStringMember("className", className);

    String colspan = cellJsonObject.getStringMember("colspan");
    if (colspan != null) {
      OptionalInt optionalInt = SwiftRuntime.parseInt(colspan);
      if (optionalInt == null) {
        cellJsonObject.setIntegerMember("columns-count", 1);
      } else if (optionalInt.value < 1) {
        cellJsonObject.setIntegerMember("columns-count", 1);
        delete(optionalInt);
      } else {
        cellJsonObject.setIntegerMember("columns-count", optionalInt.value);
        delete(optionalInt);
      }
      delete(colspan);
    } else {
      cellJsonObject.setIntegerMember("columns-count", 1);
    }

    String rowspan = cellJsonObject.getStringMember("rowspan");
    if (rowspan != null) {
      OptionalInt optionalInt = SwiftRuntime.parseInt(rowspan);
      if (optionalInt == null) {
        cellJsonObject.setIntegerMember("rows-count", 1);
      } else if (optionalInt.value < 1) {
        cellJsonObject.setIntegerMember("rows-count", 1);
        delete(optionalInt);
      } else {
        cellJsonObject.setIntegerMember("rows-count", optionalInt.value);
        delete(optionalInt);
      }
      delete(rowspan);
    } else {
      cellJsonObject.setIntegerMember("rows-count", 1);
    }
  }

  private JsonObject parseTextContents(boolean forceCreation) {

    StringBuffer stringBuffer = new StringBuffer();
    StringBuffer htmlLetterStringBuffer = null;

    while (position < input.length()) {
      if (peekNextCharacter(0) == '<') {
        if (peekString("<br>")) {
          stringBuffer.appendString("<br>");
          position += 4;
          continue;
        }
        break;
      }
      if ((peekNextCharacter(0) == '&') && ((peekNextCharacter(1) == '#'))) {
        if (htmlLetterStringBuffer != null) {
          // html letter not finished
          stringBuffer.appendString(htmlLetterStringBuffer.getString());
          delete(htmlLetterStringBuffer);
        }
        htmlLetterStringBuffer = new StringBuffer();
        position += 2;
        continue;
      }
      if (htmlLetterStringBuffer != null) {
        char c = peekCharacter();
        if (c == ';') {
          // try to convert to char
          position++;
          continue;
        }
        if (Character.isLetterOrDigit(c)) {
          htmlLetterStringBuffer.appendCharacter(c);
          position++;
          continue;
        }
        // error in html
        stringBuffer.appendString(htmlLetterStringBuffer.getString());
        delete(htmlLetterStringBuffer);
        htmlLetterStringBuffer = null;
      }
      if (peekString("&amp;")) {
        stringBuffer.appendString("&");
        position += 5;
        continue;
      }
      if (peekString("&lt;")) {
        stringBuffer.appendString("<");
        position += 4;
        continue;
      }
      if (peekString("&gt;")) {
        stringBuffer.appendString(">");
        position += 4;
        continue;
      }
      if (peekString("&quot;")) {
        stringBuffer.appendString("\"");
        position += 6;
        continue;
      }
      if (peekString("&#39;")) {
        stringBuffer.appendString("'");
        position += 5;
        continue;
      }
      if (peekString("&nbsp;")) {
        stringBuffer.appendString(" ");
        position += 6;
        continue;
      }
      if (peekString("&Aacute;")) {
        stringBuffer.appendString("Á");
        position += 8;
        continue;
      }
      if (peekString("&aacute;")) {
        stringBuffer.appendString("á");
        position += 8;
        continue;
      }
      if (peekString("&Eacute;")) {
        stringBuffer.appendString("É");
        position += 8;
        continue;
      }
      if (peekString("&eacute;")) {
        stringBuffer.appendString("é");
        position += 8;
        continue;
      }
      if (peekString("&Iacute;")) {
        stringBuffer.appendString("Í");
        position += 8;
        continue;
      }
      if (peekString("&iacute;")) {
        stringBuffer.appendString("í");
        position += 8;
        continue;
      }
      if (peekString("&Oacute;")) {
        stringBuffer.appendString("Ó");
        position += 8;
        continue;
      }
      if (peekString("&oacute;")) {
        stringBuffer.appendString("ó");
        position += 8;
        continue;
      }
      if (peekString("&Uacute;")) {
        stringBuffer.appendString("Ú");
        position += 8;
        continue;
      }
      if (peekString("&uacute;")) {
        stringBuffer.appendString("ú");
        position += 8;
        continue;
      }
      if (peekString("&Ntilde;")) {
        stringBuffer.appendString("Ñ");
        position += 8;
        continue;
      }
      if (peekString("&ntilde;")) {
        stringBuffer.appendString("ñ");
        position += 8;
        continue;
      }
      if (peekString("&copy;")) {
        stringBuffer.appendString("©");
        position += 6;
        continue;
      }
      if (peekString("&reg;")) {
        stringBuffer.appendString("®");
        position += 5;
        continue;
      }
      if (peekString("&trade;")) {
        stringBuffer.appendString("™");
        position += 7;
        continue;
      }
      if (peekString("&euro;")) {
        stringBuffer.appendString("€");
        position += 6;
        continue;
      }
      if (peekString("&pound;")) {
        stringBuffer.appendString("£");
        position += 7;
        continue;
      }
      if (peekString("&cent;")) {
        stringBuffer.appendString("¢");
        position += 6;
        continue;
      }
      if (peekString("&yen;")) {
        stringBuffer.appendString("¥");
        position += 5;
        continue;
      }

      char c = consumeCharacter();
      if (Character.isWhitespace(c)) {
        if (forceCreation) {
          stringBuffer.appendCharacter(c);
        } else if (stringBuffer.isNotEmpty()) {
          stringBuffer.appendCharacter(c);
        }
        continue;
      }
      stringBuffer.appendCharacter(c);
    }

    if (htmlLetterStringBuffer != null) {
      // not completed html character
      stringBuffer.appendString(htmlLetterStringBuffer.getString());
      delete(htmlLetterStringBuffer);
    }

    if (stringBuffer.isEmpty()) {
      delete(stringBuffer);
      return null;
    }

    JsonObject jsonObject = new JsonObject();
    jsonObject.setStringMember("tagName", "#text");
    jsonObject.setStringMember("value", stringBuffer.getString());

    delete(stringBuffer);
    return jsonObject;
  }

  private String decodeHtmlLetters(String input) {
    String result = "";
    int i = 0;

    while (i < input.length()) {
      char c = input.charAt(i);
      if (c == '&') {
        String entity = "";
        entity += c;
        i++;

        while ((i < input.length()) && (input.charAt(i) != ';') &&  || (input.charAt(i) == '#'))) {
          entity += input.charAt(i);
          i++;
        }

        if ((i < input.length()) && (input.charAt(i) == ';')) {
          entity += ';';
          i++;

          String decoded = null;
          if (entity.startsWith("&#")) {
            String string = entity.substring(2, entity.length() - 1);
            if ((string.startsWith("x")) || (string.startsWith("X"))) {
              //int codePoint = ExpectedRuntime.parseHexInteger(string.substring(1));
              //decoded = String.valueOf((char) codePoint);
            } else {
              //int codePoint = ExpectedRuntime.parseInteger(string);
              //decoded = String.valueOf((char) codePoint);
            }
          }

          if (decoded != null) {
            result += decoded;
          } else {
            result += entity;
          }
        } else {
          result += entity;
          if (i < input.length()) {
            result += input.charAt(i);
            i++;
          }
        }
      } else {
        result += c;
        i++;
      }
    }

    return result;
  }

  private void convertToTextView(JsonObject jsonObject) {

  }
}
/*
  public void buildHtmlTextBlock(Block block) {

    ExpectedList<HtmlLetterBlock> letterBlocks = new ExpectedList<>();

    accumulateHtmlLetters(block, letterBlocks);

    if (letterBlocks.isEmpty()) {
      return;
    }

    HtmlTextBlock textBlock;
    if (block instanceof HtmlTextBlock) {
      textBlock = (HtmlTextBlock) block;
      block.blocks.clear();
    } else {
      // let it be paragraph
      textBlock = new HtmlTextBlock();
      block.blocks.clear();
      block.addBlock(textBlock);
    }

    textBlock.build(letterBlocks);
  }

  private void accumulateHtmlLetters(Block block, ExpectedList<HtmlLetterBlock> htmlLetterBlocks) {
    if (block instanceof HtmlLetterBlock) {
      htmlLetterBlocks.add((HtmlLetterBlock) block);
      return;
    }
    for (int i = 0; i < block.blocks.size(); i++) {
      accumulateHtmlLetters(block.blocks.get(i), htmlLetterBlocks);
    }
  }
*/
