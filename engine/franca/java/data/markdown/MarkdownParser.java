package franca.java.data.markdown;

import franca.java.data.Parser;
import franca.java.data.html.HtmlParser;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.BlockStyle;
import franca.java.office.document.structure.HorizontalRuleBlock;
import franca.java.office.document.table.TableCellBlock;
import franca.java.office.document.typography.CharsBlock;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.ParagraphBlock;

import java.util.ArrayList;

public class MarkdownParser extends Parser {

  private HtmlParser htmlParser = new HtmlParser();

  private ArrayList<BlockStyle> blockStyles = new ArrayList<>();

  @Override
  public String getClassName() {
    return "MarkdownParser";
  }

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
        horizontalRuleBlock.text = consumeLine();
        continue;
      }
      if (peekString("---")) {
        HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
        parentBlock.addBlock(horizontalRuleBlock);
        horizontalRuleBlock.text = consumeLine();
        continue;
      }
      if (peekString("***")) {
        HorizontalRuleBlock horizontalRuleBlock = new HorizontalRuleBlock();
        parentBlock.addBlock(horizontalRuleBlock);
        horizontalRuleBlock.text = consumeLine();
        continue;
      }

      if (peekString("# ")) {
        skipChars(2);
        HeadingBlock headingBlock = new HeadingBlock(1);
        parentBlock.addBlock(headingBlock);
        parseTextContents(headingBlock, blockStyles);
        skipLine();
        continue;
      }

      if (peekString("## ")) {
        skipChars(3);
        HeadingBlock headingBlock = new HeadingBlock(2);
        parentBlock.addBlock(headingBlock);
        parseTextContents(headingBlock, blockStyles);
        skipLine();
        continue;
      }

      if (peekString("### ")) {
        skipChars(4);
        HeadingBlock headingBlock = new HeadingBlock(3);
        parentBlock.addBlock(headingBlock);
        parseTextContents(headingBlock, blockStyles);
        skipLine();
        continue;
      }

      if (peekString("#### ")) {
        skipChars(5);
        HeadingBlock headingBlock = new HeadingBlock(4);
        parentBlock.addBlock(headingBlock);
        parseTextContents(headingBlock, blockStyles);
        skipLine();
        continue;
      }

      if (peekString("##### ")) {
        skipChars(6);
        HeadingBlock headingBlock = new HeadingBlock(5);
        parentBlock.addBlock(headingBlock);
        parseTextContents(headingBlock, blockStyles);
        skipLine();
        continue;
      }

      if (peekString("###### ")) {
        skipChars(7);
        HeadingBlock headingBlock = new HeadingBlock(6);
        parentBlock.addBlock(headingBlock);
        parseTextContents(headingBlock, blockStyles);
        skipLine();
        continue;
      }

      Block block = htmlParser.parseHtmlNode();;
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

  public void parseTextContents(Block parentBlock, ArrayList<BlockStyle> blockStyles) {

    literalBufferedString = new BufferedString();

    // for trailing spaces
    int spacesCount = 0;

    while (inputPosition < input.length()) {

      BlockStyle blockStyle;
      if (blockStyles.isEmpty()) {
        blockStyle = null;
      } else {
        blockStyle = blockStyles.get(blockStyles.size() - 1);
      }

      if (parseBlockStyle(blockStyles)) {
        if (spacesCount > 0) {
          // we accumulated spaces
          parentBlock = htmlParser.appendSpaceBlocks(parentBlock, spacesCount, blockStyle);
        } else if (literalBufferedString.isNotEmpty()) {
          parentBlock = htmlParser.appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
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
          parentBlock = htmlParser.appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
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
          parentBlock = htmlParser.appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
        }
        literalBufferedString.clear();
        parentBlock = htmlParser.appendSpaceBlocks(parentBlock, spacesCount, blockStyle);
      }

      spacesCount = 0;

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
      htmlParser.appendCharsBlock(parentBlock, CharsBlock.TYPE_CHARS, literalBufferedString.getString(), blockStyle);
    }
  }

  public boolean parseBlockStyle(ArrayList<BlockStyle> blockStyles) {
    return false;
  }
}
