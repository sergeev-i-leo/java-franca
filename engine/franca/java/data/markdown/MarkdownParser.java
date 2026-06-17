package franca.java.data.markdown;

import java.util.ArrayList;

import franca.java.data.html.HtmlParser;
import franca.java.data.json.JsonObject;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.structure.HorizontalRuleBlock;
import franca.java.office.document.table.TableCellBlock;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;

public class MarkdownParser extends HtmlParser {

  private ArrayList<JsonObject> styleJsonObjects = new ArrayList<>();

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
        skipLineEnd();
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

      if (peekString("# ")) {
        skipChars(2);
        HeadingBlock headingBlock = new HeadingBlock(1);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, styleJsonObjects);
        skipLine();
        continue;
      }

      if (peekString("## ")) {
        skipChars(3);
        HeadingBlock headingBlock = new HeadingBlock(2);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, styleJsonObjects);
        skipLine();
        continue;
      }

      if (peekString("### ")) {
        skipChars(4);
        HeadingBlock headingBlock = new HeadingBlock(3);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, styleJsonObjects);
        skipLine();
        continue;
      }

      if (peekString("#### ")) {
        skipChars(5);
        HeadingBlock headingBlock = new HeadingBlock(4);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, styleJsonObjects);
        skipLine();
        continue;
      }

      if (peekString("##### ")) {
        skipChars(6);
        HeadingBlock headingBlock = new HeadingBlock(5);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, styleJsonObjects);
        skipLine();
        continue;
      }

      if (peekString("###### ")) {
        skipChars(7);
        HeadingBlock headingBlock = new HeadingBlock(6);
        parentBlock.addBlock(headingBlock);
        parseMarkdownTextContents(headingBlock, styleJsonObjects);
        skipLine();
        continue;
      }

      Block block = parseHtmlNode();;
      if (block != null) {
        // skip lineEnd
        parentBlock.addBlock(block);
        // empty line after embedded html
        if (peekLineEnd()) {
          skipLineEnd();
        }
        continue;
      }
      BufferedString bufferedString = new BufferedString();
      while (inputPosition < input.length()) {
        if (peekLineEnd()) {
          skipLineEnd();
          break;
        }
        bufferedString.appendChar(consumeChar());
      }
      // treat unknown block as paragraph block
      ParagraphBlock paragraphBlock = new ParagraphBlock();
      parentBlock.addBlock(paragraphBlock);
      paragraphBlock.setMarkdownText(bufferedString.getString());
    }
  }

  public void parseMarkdownTextContents(Block parentBlock, ArrayList<JsonObject> styleJsonObjects) {

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
        skipLineEnd();
        break;
      }

      if ((parentBlock instanceof TableCellBlock) && (peekChar() == '|')) {
        consumeChar();
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
