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

    return parseViews(null, null);
  }

  public JsonArray parseViews(JsonArray parentJsonArray, String parentTagName) {

    JsonArray jsonArray = new JsonArray();
    while (true) {

      if (position >= input.length()) {
        break;
      }

      if (peekCharacter() != '<') {
        // collect text nodes
        JsonObject spanJsonObject = new JsonObject();
        spanJsonObject = parseSpanView(spanJsonObject);
        if (spanJsonObject != null) {
          if (parentJsonArray != null) {
            parentJsonArray.addElement(spanJsonObject);
          } else {
            jsonArray.addElement(spanJsonObject);
          }
          continue;
        }
      }

      if (peekNextCharacter(1) == '/') {
        // /
        consumeCharacter();
        // >
        consumeCharacter();
        String tagName = parseTagName();
        // >
        consumeCharacter();
        if (tagName.equals(parentTagName)) {
          delete(tagName);
          tagName = null;
          break;
        }
      }

      consumeCharacter();

      String tagName = parseTagName();
      if (tagName.equals("span")) {
        delete(tagName);

        JsonObject spanJsonObject = new JsonObject();
        parseHtmlAttributes(spanJsonObject);
        consumeCharacter();
        spanJsonObject = parseSpanView(spanJsonObject);
        if (parentJsonArray != null) {
          parentJsonArray.addElement(spanJsonObject);
        } else {
          jsonArray.addElement(spanJsonObject);
        }
        // /
        consumeCharacter();
        // >
        consumeCharacter();
        delete(parseTagName());
        // >
        consumeCharacter();
        continue;
      }
      JsonObject jsonObject = null;
      switch (tagName) {
        case "img":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "image-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "table":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "table-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "tbody":
          if (parentJsonArray != null) {
            parseViews(parentJsonArray, tagName);
          } else {
            parseViews(jsonArray, tagName);
          }
          break;
        case "tr":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "table-row-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "th":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          stuffCellJsonObject(jsonObject, "table-header-cell-view");
          break;
        case "td":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          stuffCellJsonObject(jsonObject, "table-cell-view");
          break;
        case "h1":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "typography-h1-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "h2":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "typography-h2-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "h3":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "typography-h3-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "h4":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "typography-h4-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "h5":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "typography-h5-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "h6":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "typography-h6-view");
          parseHtmlAttributes(jsonObject);
          break;
        case "p":
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          jsonObject.setStringMember("className", "typography-paragraph-view");
          parseHtmlAttributes(jsonObject);
          break;
        default:
          jsonObject = new JsonObject();
          jsonObject.setStringMember("tagName", tagName);
          parseHtmlAttributes(jsonObject);
          break;
      }

      if ((peekCharacter() == '/') && (peekNextCharacter(1) == '>')) {
        // self-closing
        consumeCharacter();
        consumeCharacter();
      } else if (tagName.equals("img")) {
        // self-closing
        consumeCharacter();
      } else if (jsonObject != null) {
        consumeCharacter();
        // add children
        JsonArray viewsJsonArray = new JsonArray();
        jsonObject.setMember("views", viewsJsonArray);
        parseViews(viewsJsonArray, tagName);
        // children may be text
        convertToTextView(jsonObject);
      }

      delete(tagName);

      if (jsonObject != null) {
        if (parentJsonArray != null) {
          parentJsonArray.addElement(jsonObject);
        } else {
          jsonArray.addElement(jsonObject);
        }
      }
    }
    if (parentJsonArray != null) {
      delete(jsonArray);
      return parentJsonArray;
    }
    return jsonArray;
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

  private void stuffCellJsonObject(JsonObject cellJsonObject, String className) {
    cellJsonObject.setStringMember("className", className);
    parseHtmlAttributes(cellJsonObject);

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

  private JsonObject parseSpanView(JsonObject spanJsonObject) {
    String styleColor = spanJsonObject.getStringMember("style.color");

    boolean returnSpanJsonObject = false;

    SwiftStringBuilder swiftStringBuilder = new SwiftStringBuilder();
    while (position < input.length()) {
      if (peekCharacter() == '<') {
        // consume <br>
        if (input.indexOf("<br>", position) != position) {
          break;
        } else {
          returnSpanJsonObject = true;
          swiftStringBuilder.appendString("\\n");
          position += 4;
          continue;
        }
      }
      char c = consumeCharacter();
      if (!Character.isWhitespace(c)) {
        returnSpanJsonObject = true;
      }
      swiftStringBuilder.appendCharacter(c);
    }

    if (!swiftStringBuilder.isEmpty()) {
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

  private String decodeHtmlLetters(String html) {
    String result = "";
    int i = 0;

    while (i < html.length()) {
      char c = html.charAt(i);
      if (c == '&') {
        String entity = "";
        entity += c;
        i++;

        while ((i < html.length()) && (html.charAt(i) != ';') && (Character.isLetterOrDigit(html.charAt(i)) || (html.charAt(i) == '#'))) {
          entity += html.charAt(i);
          i++;
        }

        if ((i < html.length()) && (html.charAt(i) == ';')) {
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
          if (i < html.length()) {
            result += html.charAt(i);
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
