package franca.java.office.document.typography;

import franca.java.data.html.HtmlParser;
import franca.java.data.markdown.MarkdownParser;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.BlockStyle;

import java.util.ArrayList;

public class TextBlock extends Block {

  public String getDataBlock() {
    return "TextBlock";
  }

  public void setText(String text) {
    clearBlocks();

    BufferedString bufferedString = new BufferedString();
    int textPosition = 0;
    while (textPosition < text.length()) {
      char c = text.charAt(textPosition);
      if (c == ' ') {
        if (bufferedString.isNotEmpty()) {
          CharsBlock charsBlock = new CharsBlock();
          parentBlock.addBlock(charsBlock);
          charsBlock.type = CharsBlock.TYPE_CHARS;
          charsBlock.setChars(bufferedString.getString());
          bufferedString.clear();
        }
        CharsBlock charsBlock = new CharsBlock();
        parentBlock.addBlock(charsBlock);
        charsBlock.type = CharsBlock.TYPE_SPACE;
        charsBlock.setChars(" ");
      }
      textPosition++;
    }
    if (bufferedString.isNotEmpty()) {
      CharsBlock charsBlock = new CharsBlock();
      parentBlock.addBlock(charsBlock);
      charsBlock.type = CharsBlock.TYPE_CHARS;
      charsBlock.setChars(bufferedString.getString());
    }
  }

  public void setHtmlText(String htmlText) {
    clearBlocks();

    ArrayList<BlockStyle> blockStyles = new ArrayList<>();
    HtmlParser htmlParser = new HtmlParser();
    htmlParser.input = htmlText;
    htmlParser.inputPosition = 0;
    htmlParser.parseHtmlTextContents(this, blockStyles);
  }

  public void setMarkdownText(String markdownText) {
    clearBlocks();

    ArrayList<BlockStyle> blockStyles = new ArrayList<>();
    MarkdownParser markdownParser = new MarkdownParser();
    markdownParser.input = markdownText;
    markdownParser.inputPosition = 0;
    markdownParser.parseMarkdownTextContents(this, blockStyles);
  }

  public String getText() {
    if (getBlocks() == null) {
      return "";
    }
    BufferedString bufferedString = new BufferedString();
    for (Block block : getBlocks()) {
      if (block instanceof CharsBlock) {
        bufferedString.appendString(((CharsBlock) block).getChars());
      }
    }
    return bufferedString.getString();
  }
}
