package franca.java.data.markdown;

import java.util.ArrayList;

import franca.java.data.html.HtmlParser;
import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.list.ListBlock;
import franca.java.office.document.list.ListItemBlock;
import franca.java.office.document.structure.HorizontalRuleBlock;
import franca.java.office.document.table.*;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;

public class MarkdownParser extends HtmlParser {

  public Block parse(String input) {
    this.input = input;

    inputPosition = 0;

    Block block = new Block();

    parseMarkdownBlocks(block);

    return block;
  }

  public void parseMarkdownBlocks(Block parentBlock) {
    while (inputPosition < input.length()) {
      parseMarkdownBlock(parentBlock);
    }
  }

  public void parseMarkdownBlock(Block parentBlock) {
    // \r\n
    if (peekLineEnd()) {
      parentBlock.addChildBlock(new ParagraphBlock());
      skipLine();
      return;
    }
    if (peekString("___")) {
      HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
      parentBlock.addChildBlock(horizontalRuleBlock);
      horizontalRuleBlock.type = consumeLine();
      return;
    }
    if (peekString("---")) {
      HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
      parentBlock.addChildBlock(horizontalRuleBlock);
      horizontalRuleBlock.type = consumeLine();
      return;
    }
    if (peekString("***")) {
      HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
      parentBlock.addChildBlock(horizontalRuleBlock);
      horizontalRuleBlock.type = consumeLine();
      return;
    }

    if (peekString("###### ")) {
      skipChars(7);
      HeadingBlock headingBlock = new HeadingBlock(6);
      parentBlock.addChildBlock(headingBlock);
      parseMarkdownTextContents(headingBlock, false);
      skipLine();
      return;
    }

    if (peekString("##### ")) {
      skipChars(6);
      HeadingBlock headingBlock = new HeadingBlock(5);
      parentBlock.addChildBlock(headingBlock);
      parseMarkdownTextContents(headingBlock, false);
      skipLine();
      return;
    }

    if (peekString("#### ")) {
      skipChars(5);
      HeadingBlock headingBlock = new HeadingBlock(4);
      parentBlock.addChildBlock(headingBlock);
      parseMarkdownTextContents(headingBlock, false);
      skipLine();
      return;
    }

    if (peekString("### ")) {
      skipChars(4);
      HeadingBlock headingBlock = new HeadingBlock(3);
      parentBlock.addChildBlock(headingBlock);
      parseMarkdownTextContents(headingBlock, false);
      skipLine();
      return;
    }

    if (peekString("## ")) {
      skipChars(3);
      HeadingBlock headingBlock = new HeadingBlock(2);
      parentBlock.addChildBlock(headingBlock);
      parseMarkdownTextContents(headingBlock, false);
      skipLine();
      return;
    }

    if (peekString("# ")) {
      skipChars(2);
      HeadingBlock headingBlock = new HeadingBlock(1);
      parentBlock.addChildBlock(headingBlock);
      parseMarkdownTextContents(headingBlock, false);
      skipLine();
      return;
    }

    Block block = parseMarkdownListBlock(0);
    if (block != null) {
      if (parentBlock != null) {
        parentBlock.addChildBlock(block);
      }
      // empty line after list block to be parsed to paragraph block
      return;
    }

    block = parseMarkdownTableBlock();
    if (block != null) {
      if (parentBlock != null) {
        parentBlock.addChildBlock(block);
      }
      // empty line after table block to be parsed to paragraph block
      return;
    }

    block = parseHtmlNode();;
    if (block != null) {
      // skip lineEnd
      parentBlock.addChildBlock(block);
      // empty line after embedded html
      if (peekLineEnd()) {
        skipLineEnd();
      }
      return;
    }

    // paragraph block
    ParagraphBlock paragraphBlock = new ParagraphBlock();
    parentBlock.addChildBlock(paragraphBlock);
    parseMarkdownTextContents(paragraphBlock, false);
    skipLine();
  }

  public ListBlock parseMarkdownListBlock(int expectedIndentationCount) {
    ListBlock resultListBlock = null;
    ListItemBlock listItemBlock = null;

    while (true) {
      int storedPosition = inputPosition;

      int foundIndentationCount = 0;
      while (peekChar() == ' ') {
        foundIndentationCount++;
        skipChars(1);
      }

      if (foundIndentationCount == expectedIndentationCount) {
        // item
        listItemBlock = parseMarkdownListItemBlock();
        if (listItemBlock == null) {
          // not a list item
          inputPosition = storedPosition;
          break;
        }

        if (resultListBlock == null) {
          switch (listItemBlock.type.charAt(0)) {
            case '*':
            case '-':
            case '+':
              resultListBlock = new ListBlock(false);
              break;
            default:
              resultListBlock = new ListBlock(true);
              break;
          }
        }
        resultListBlock.addChildBlock(listItemBlock);
        continue;
      }

      inputPosition = storedPosition;

      if (foundIndentationCount < expectedIndentationCount) {
        // level up
        break;
      }

      // level down
      if (listItemBlock == null) {
        break;
      }

      Block includedListBlock = parseMarkdownListBlock(foundIndentationCount);
      if (includedListBlock != null) {
        listItemBlock.addChildBlock(includedListBlock);
      }
    }

    return resultListBlock;
  }

  public ListItemBlock parseMarkdownListItemBlock() {
    int storedPosition = inputPosition;

    ListItemBlock listItemBlock = null;

    switch (peekChar()) {
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        break;
      case '*':
        listItemBlock = new ListItemBlock();
        listItemBlock.type = "*";
        skipChars(1);
        break;
      case '-':
        listItemBlock = new ListItemBlock();
        listItemBlock.type = "-";
        skipChars(1);
        break;
      case '+':
        listItemBlock = new ListItemBlock();
        listItemBlock.type = "+";
        skipChars(1);
        break;
      default:
        return null;
    }

    if (listItemBlock == null) {
      // collect
      literalBufferedString = new BufferedString();
      while (inputPosition < input.length()) {
        char c = peekChar();
        if (c == '.') {
          literalBufferedString.appendChar(c);
          skipChars(1);
          break;
        }
        switch (c) {
          case '0':
          case '1':
          case '2':
          case '3':
          case '4':
          case '5':
          case '6':
          case '7':
          case '8':
          case '9':
            literalBufferedString.appendChar(c);
            skipChars(1);
            break;
          default:
            return null;
        }
      }
      listItemBlock = new ListItemBlock();
      listItemBlock.type = literalBufferedString.getString();
    }

    if (peekChar() != ' ') {
      inputPosition = storedPosition;
      return null;
    }

    skipChars(1);

    parseMarkdownTextContents(listItemBlock, false);
    skipLine();

    return listItemBlock;
  }

  public TableBlock parseMarkdownTableBlock() {
    TableBlock tableBlock = null;
    TableHeaderBlock tableHeaderBlock = null;
    TableBodyBlock tableBodyBlock = null;

    ArrayList<String> blockCellAlignments = null;
    while (inputPosition < input.length()) {
      if (peekChar() != '|') {
        break;
      }

      skipChars(1);
      skipWhitespaces();

      if (peekString(":-")) {
        blockCellAlignments = new ArrayList<>();
        parseBlockCellAlignments(blockCellAlignments);
        continue;
      }
      if (peekString("-")) {
        blockCellAlignments = new ArrayList<>();
        parseBlockCellAlignments(blockCellAlignments);
        continue;
      }

      if (tableBlock == null) {
        tableBlock = new TableBlock();
      }

      TableRowBlock tableRowBlock = new TableRowBlock();

      if (tableHeaderBlock == null) {
        tableHeaderBlock = new TableHeaderBlock();
        tableBlock.addChildBlock(tableHeaderBlock);
        tableHeaderBlock.addChildBlock(tableRowBlock);
      } else {
        if (tableBodyBlock == null) {
          tableBodyBlock = new TableBodyBlock();
          tableBlock.addChildBlock(tableBodyBlock);
        }
        tableBodyBlock.addChildBlock(tableRowBlock);
      }

      while (inputPosition < input.length()) {
        if (peekLineEnd()) {
          skipLine();
          break;
        }
        TableCellBlock tableCellBlock = new TableCellBlock(tableBodyBlock == null);
        parseMarkdownTextContents(tableCellBlock, true);
        skipWhitespaces();
        if (peekChar() == '|') {
          tableRowBlock.addChildBlock(tableCellBlock);
        }
        // '|'
        skipChars(1);
      }
      if ((tableHeaderBlock != null) && (tableBodyBlock == null)) {
        for (int i = 0; i < tableRowBlock.getChildBlocks().size(); i++) {
          Block block = tableRowBlock.getChildBlock(i);
          if (block != null) {
            block = block.getChildBlock(0);
            if (block != null) {
              block.styleJsonObject.putStringValue("text-align", "center");
            }
          }
        }
      }
      // alignment
      if (blockCellAlignments == null) {
        continue;
      }
      for (int i = 0; i < blockCellAlignments.size(); i++) {
        Block block = tableRowBlock.getChildBlock(i);
        if (block != null) {
          block = block.getChildBlock(0);
          if (block != null) {
            block.styleJsonObject.putStringValue("text-align", blockCellAlignments.get(i));
          }
        }
      }
    }
    return tableBlock;
  }

  private void parseBlockCellAlignments(ArrayList<String> blockCellAlignments) {
    String blockCellAlignment = "default";
    while (inputPosition < input.length()) {
      if (peekLineEnd()) {
        skipLine();
        break;
      }
      if (peekString(":-")) {
        blockCellAlignment = "left";
        skipChars(1);
        continue;
      }
      if (peekString("-:")) {
        if (blockCellAlignment.equals("left")) {
          blockCellAlignment = "center";
        } else {
          blockCellAlignment = "right";
        }
      }
      if (peekChar() != '|') {
        skipChars(1);
        continue;
      }
      if (blockCellAlignment.equals("default")) {
        blockCellAlignment = "left";
      }
      blockCellAlignments.add(blockCellAlignment);
      blockCellAlignment = "default";
      // '|'
      skipChars(1);
    }
  }

  public void parseMarkdownTextContents(Block parentBlock, boolean isTableCell) {

    // stops at line end

    int textInputPosition = inputPosition;

    ArrayList<JsonObject> styleJsonObjects = new ArrayList<>();

    literalBufferedString = new BufferedString();

    // for trailing spaces
    int spacesCount = 0;

    while (inputPosition < input.length()) {

      JsonObject styleJsonObject;
      if (styleJsonObjects.isEmpty()) {
        styleJsonObject = null;
      } else {
        styleJsonObject = styleJsonObjects.get(styleJsonObjects.size() - 1);
      }

      if (parseMarkdownTextContentsStyle(textInputPosition, styleJsonObjects)) {
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

      if (peekLineEnd()) {
        break;
      }

      if (isTableCell) {
        if ((peekChar() == '\\') && (peekNextChar(1) == '|')) {
          // escaped |, consume later
          skipChars(1);
        } else if (peekChar() == '|') {
          break;
        }
      }

      if (peekChar() == ' ') {
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

      spacesCount = 0;

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

  public boolean parseMarkdownTextContentsStyle(int textInputPosition, ArrayList<JsonObject> styleJsonObjects) {
    if (styleJsonObjects.size() > 0) {
      // end of style ?
      if (peekString("**") && (inputPosition > textInputPosition) && (!isWhitespace(peekNextChar(-1)))) {
        if (isDelimiter(peekNextChar(2))) {
          // end of style
          skipChars(2);
          styleJsonObjects.remove(styleJsonObjects.size() - 1);
        }
        return true;
      }
      if (peekString("*") && (inputPosition > textInputPosition) && (!isWhitespace(peekNextChar(-1)))) {
        if (isDelimiter(peekNextChar(1))) {
          // end of style
          skipChars(1);
          styleJsonObjects.remove(styleJsonObjects.size() - 1);
          return true;
        }
      }
    }
    if (peekChar() != '*') {
      return false;
    }
    JsonObject styleJsonObject;
    if (styleJsonObjects.isEmpty()) {
      styleJsonObject = new JsonObject();
    } else {
      styleJsonObject = styleJsonObjects.get(styleJsonObjects.size() - 1);
      styleJsonObject = styleJsonObject.createCopy().asJsonObject();
    }
    if ((peekString("**")) && (!isWhitespace(peekNextChar(2)))) {
      // start of bold
      styleJsonObject.putStringValue("font-weight", "700");
      styleJsonObjects.add(styleJsonObject);
      skipChars(2);
      return true;
    }
    if ((peekString("*")) && (!isWhitespace(peekNextChar(2)))) {
      // start of bold
      styleJsonObject.putStringValue("font-style", "italic");
      styleJsonObjects.add(styleJsonObject);
      skipChars(1);
      return true;
    }
    return false;
  }

  public boolean isDelimiter(char c) {
    if (c <= 32) {
      return true;
    }

    switch (c) {
      case ',':
      case '.':
      case ';':
      case ':':
      case '?':
      case '!':
      case '/':
      case '\\':
      case '(':
      case ')':
      case '[':
      case ']':
      case '{':
      case '}':
      case '<':
      case '>':
      case '\'':
      case '"':
      case '`':
      case '~':
      case '@':
      case '#':
      case '$':
      case '%':
      case '^':
      case '&':
      case '|':
      case '+':
      case '=':
      case '-':
      case '_':
        return true;
      default:
        return false;
    }
  }
}
