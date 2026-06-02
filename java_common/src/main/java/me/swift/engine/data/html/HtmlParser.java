package me.swift.engine.data.html;

import me.swift.engine.contract.OptionalInt;
import me.swift.engine.contract.SwiftRuntime;
import me.swift.engine.contract.SwiftStringBuilder;
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
    SwiftStringBuilder swiftStringBuilder = new SwiftStringBuilder();
    while ((position < input.length()) && (Character.isLetterOrDigit(peekCharacter()))) {
      char c = consumeCharacter();
      swiftStringBuilder.appendCharacter(c);
    }
    String string = swiftStringBuilder.getLowerCaseString();
    delete(swiftStringBuilder);
    return string;
  }

  private void parseHtmlNodeContents(String tagName, JsonArray viewsJsonArray) {
    JsonObject jsonObject = parseTextContents();

  }

  private void parseHtmlAttributes(JsonObject jsonObject) {

    skipWhitespaces();

    while ((position < input.length()) && (peekCharacter() != '>') && (peekCharacter() != '/')) {

      // attribute name
      SwiftStringBuilder keySwiftStringBuilder = new SwiftStringBuilder();
      while ((position < input.length()) && (isAttributeNameChar(peekCharacter()))) {
        keySwiftStringBuilder.appendCharacter(consumeCharacter());
      }

      skipWhitespaces();

      SwiftStringBuilder valueSwiftStringBuilder = new SwiftStringBuilder();
      if (peekCharacter() == '=') {
        consumeCharacter();

        skipWhitespaces();

        char quote = peekCharacter();
        if ((quote == '"') || (quote == '\'')) {
          // skip quote
          consumeCharacter();

          while ((position < input.length()) && (peekCharacter() != quote)) {
            valueSwiftStringBuilder.appendCharacter(consumeCharacter());
          }

          // skip quote
          consumeCharacter();
        } else {
          while ((position < input.length()) && (isAttributeValueChar(peekCharacter()))) {
            valueSwiftStringBuilder.appendCharacter(consumeCharacter());
          }
        }
        if (keySwiftStringBuilder.isEmpty()) {
          delete(keySwiftStringBuilder);
          delete(valueSwiftStringBuilder);
          skipWhitespaces();
          continue;
        }
        String attributeName = keySwiftStringBuilder.getLowerCaseString();
        if (attributeName.equals("style")) {
          String[] styles = valueSwiftStringBuilder.getString().split(";");
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
          jsonObject.setStringMember(attributeName, valueSwiftStringBuilder.getString());
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
    SwiftStringBuilder swiftStringBuilder = new SwiftStringBuilder();
    while (position < input.length()) {
      if (peekNextCharacter(0) == '<') {
        if ((peekNextCharacter(1) == 'b') && (peekNextCharacter(2) == 'r') && (peekNextCharacter(3) == '>')) {
          swiftStringBuilder.appendString("<br>");
          position += 4;
          continue;
        }
        break;
      }

      char c = consumeCharacter();
      if (Character.isWhitespace(c)) {
        if (forceCreation) {
          swiftStringBuilder.appendCharacter(c);
        } else if (swiftStringBuilder.isNotEmpty()) {
          swiftStringBuilder.appendCharacter(c);
        }
        continue;
      }
      swiftStringBuilder.appendCharacter(c);
    }

    if (swiftStringBuilder.isEmpty()) {
      return null;
    }
    JsonObject jsonObject = new JsonObject();
    jsonObject.setStringMember("tagName", "#text");
    jsonObject.setStringMember("value", "#text");
      spanJsonObject.setStringMember("text", swiftStringBuilder.getString());
      /*text = decodeHtmlLetters(text);

      for (int i = 0; i < text.length(); i++) {

        HtmlLetterBlock htmlLetterBlock = new HtmlLetterBlock();
        spanBlock.addBlock(htmlLetterBlock);
        htmlLetterBlock.text = String.valueOf(text.charAt(i));
        htmlLetterBlock.styleColor = styleColor;
        if ("#000000".equals(htmlLetterBlock.styleColor)) {
          htmlLetterBlock.styleColor = null;
        }
      }*/
    }

    if (!returnSpanJsonObject) {
      // <span> </span>
      delete(spanJsonObject);
      return null;
    }
    return spanJsonObject;
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

        while ((i < input.length()) && (input.charAt(i) != ';') && (Character.isLetterOrDigit(input.charAt(i)) || (input.charAt(i) == '#'))) {
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
          } else if (entity.equals("&amp;")) {
            decoded = "&";
          } else if (entity.equals("&lt;")) {
            decoded = "<";
          } else if (entity.equals("&gt;")) {
            decoded = ">";
          } else if (entity.equals("&quot;")) {
            decoded = "\"";
          } else if (entity.equals("&#39;")) {
            decoded = "'";
          } else if (entity.equals("&nbsp;")) {
            decoded = " ";
          } else if (entity.equals("&Aacute;")) {
            decoded = "Á";
          } else if (entity.equals("&aacute;")) {
            decoded = "á";
          } else if (entity.equals("&Eacute;")) {
            decoded = "É";
          } else if (entity.equals("&eacute;")) {
            decoded = "é";
          } else if (entity.equals("&Iacute;")) {
            decoded = "Í";
          } else if (entity.equals("&iacute;")) {
            decoded = "í";
          } else if (entity.equals("&Oacute;")) {
            decoded = "Ó";
          } else if (entity.equals("&oacute;")) {
            decoded = "ó";
          } else if (entity.equals("&Uacute;")) {
            decoded = "Ú";
          } else if (entity.equals("&uacute;")) {
            decoded = "ú";
          } else if (entity.equals("&Ntilde;")) {
            decoded = "Ñ";
          } else if (entity.equals("&ntilde;")) {
            decoded = "ñ";
          } else if (entity.equals("&copy;")) {
            decoded = "©";
          } else if (entity.equals("&reg;")) {
            decoded = "®";
          } else if (entity.equals("&trade;")) {
            decoded = "™";
          } else if (entity.equals("&euro;")) {
            decoded = "€";
          } else if (entity.equals("&pound;")) {
            decoded = "£";
          } else if (entity.equals("&cent;")) {
            decoded = "¢";
          } else if (entity.equals("&yen;")) {
            decoded = "¥";
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
