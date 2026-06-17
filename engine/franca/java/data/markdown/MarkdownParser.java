package franca.java.data.markdown;

import java.util.ArrayList;

import franca.java.data.html.HtmlParser;
import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.list.ListBlock;
import franca.java.office.document.list.ListItemBlock;
import franca.java.office.document.structure.HorizontalRuleBlock;
import franca.java.office.document.table.TableBlock;
import franca.java.office.document.table.TableCellBlock;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;

public class MarkdownParser extends HtmlParser {

  private final ArrayList<JsonObject> styleJsonObjects = new ArrayList<>();

  public Block parse(String input) {
    this.input = input;

    inputPosition = 0;

    Block block = new Block();

    parseMarkdownBlocks(block);

    return block;
  }

  void parseMarkdownBlocks(Block parentBlock) {
    while (inputPosition < input.length()) {
      // \r\n
      if (peekLineEnd()) {
        parentBlock.addBlock(new ParagraphBlock());
        skipLine();
        continue;
      }
      if (peekString("___")) {
        HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
        parentBlock.addBlock(horizontalRuleBlock);
        horizontalRuleBlock.type = consumeLine();
        continue;
      }
      if (peekString("---")) {
        HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
        parentBlock.addBlock(horizontalRuleBlock);
        horizontalRuleBlock.type = consumeLine();
        continue;
      }
      if (peekString("***")) {
        HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
        parentBlock.addBlock(horizontalRuleBlock);
        horizontalRuleBlock.type = consumeLine();
        continue;
      }

      if (peekString("###### ")) {
        skipChars(7);
        HeadingBlock headingBlock = new HeadingBlock(6);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, false);
        skipLine();
        continue;
      }

      if (peekString("##### ")) {
        skipChars(6);
        HeadingBlock headingBlock = new HeadingBlock(5);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, false);
        skipLine();
        continue;
      }

      if (peekString("#### ")) {
        skipChars(5);
        HeadingBlock headingBlock = new HeadingBlock(4);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, false);
        skipLine();
        continue;
      }

      if (peekString("### ")) {
        skipChars(4);
        HeadingBlock headingBlock = new HeadingBlock(3);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, false);
        skipLine();
        continue;
      }

      if (peekString("## ")) {
        skipChars(3);
        HeadingBlock headingBlock = new HeadingBlock(2);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, false);
        skipLine();
        continue;
      }

      if (peekString("# ")) {
        skipChars(2);
        HeadingBlock headingBlock = new HeadingBlock(1);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, false);
        skipLine();
        continue;
      }

      Block block = parseMarkdownListBlock(0);
      if (block != null) {
        if (block != null) {
          parentBlock.addBlock(block);
        }
        // empty line after list
        if (peekLineEnd()) {
          skipLineEnd();
        }
        continue;
      }
/*
      block = parseMarkdownTableBlock();
      if (block != null) {
        if (block != null) {
          parentBlock.addBlock(block);
        }
        // empty line after list
        if (peekLineEnd()) {
          skipLineEnd();
        }
        continue;
      }
*/
      block = parseHtmlNode();;
      if (block != null) {
        // skip lineEnd
        parentBlock.addBlock(block);
        // empty line after embedded html
        if (peekLineEnd()) {
          skipLineEnd();
        }
        continue;
      }

      // treat unknown block as paragraph block

      literalBufferedString = new BufferedString();
      while (inputPosition < input.length()) {
        if (peekLineEnd()) {
          skipLineEnd();
          break;
        }
        literalBufferedString.appendChar(consumeChar());
      }
      ParagraphBlock paragraphBlock = new ParagraphBlock();
      parentBlock.addBlock(paragraphBlock);
      paragraphBlock.setMarkdownText(literalBufferedString.getString());
    }
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
        listItemBlock = parseMarkdownListItemBlock();
        if (listItemBlock == null) {
          // not a list item
          inputPosition = storedPosition;
          return resultListBlock;
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
        resultListBlock.addBlock(listItemBlock);
        continue;
      }

      if (foundIndentationCount < expectedIndentationCount) {
        // level up
        inputPosition = storedPosition;
        return resultListBlock;
      }

      // level down
      var includedListBlock = parseMarkdownListBlock(foundIndentationCount);
      if ((listItemBlock != null) && (includedListBlock != null)) {
        listItemBlock.addBlock(includedListBlock);
      }
    }
  }

  public ListItemBlock parseMarkdownListItemBlock() {
    ListItemBlock listItemBlock;
    if (peekNextChar(1) == '.') {
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
          listItemBlock = new ListItemBlock();
          break;
        default:
          return null;
      }
    } else {
      switch (peekChar()) {
        case '*':
        case '-':
        case '+':
          listItemBlock = new ListItemBlock();
          break;
        default:
          return null;
      }
    }

    while (peekChar() != ' ') {
      if (peekLineEnd()) {
        // empty
        return listItemBlock;
      }
      listItemBlock.type += consumeChar();
    }

    // ' '

    skipChars(1);

    parseMarkdownTextContents(listItemBlock, false);
    skipLine();

    return listItemBlock;
  }

  /*public TableBlock parseMarkdownTableBlock() {
    Table

  }*/

  public void parseMarkdownTextContents(Block parentBlock, boolean isTableCell) {

    // stops at line end

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

      if (parseMarkdownTextContentsStyle(styleJsonObjects)) {
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

      if (peekLineEnd()) {
        break;
      }

      if ((isTableCell) && (peekChar() == '|')) {
        break;
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

  public boolean parseMarkdownTextContentsStyle(ArrayList<JsonObject> styleJsonObjects) {
    return false;
  }
}
