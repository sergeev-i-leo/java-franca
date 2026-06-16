package franca.java.data.markdown;

import franca.java.data.html.HtmlParser;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.typography.ParagraphBlock;

public class MarkdownParser extends HtmlParser {

  public MarkdownParser() {
    super();
    skipLeadingSpaces = false;
  }

  @Override
  public String getClassName() {
    return "MarkdownParser";
  }

  @Override
  public Block parse(String input, BufferedString outputBufferedString) {
    this.input = input;
    this.outputBufferedString = outputBufferedString;

    Block block = new Block();

    parseMarkdownBlocks(block);

    return block;
  }

  void parseMarkdownBlocks(Block parentBlock) {
    while (inputPosition < input.length()) {
      if (peekLineEnd()) {
        parentBlock.addBlock(new ParagraphBlock());
        skipLineEnd();
        continue;
      }
      Block block = parseHtmlNode();
      if (block != null) {
        parentBlock.addBlock(block);
        skipLine();
        // empty line after embedded html
        if (peekLineEnd()) {
          skipLineEnd();
        }
        continue;
      }
    }
  }
}
