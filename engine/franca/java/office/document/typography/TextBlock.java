package franca.java.office.document.typography;

import franca.java.data.html.HtmlParser;
import franca.java.data.json.JsonObject;
import franca.java.data.markdown.MarkdownParser;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;

import java.util.ArrayList;

public class TextBlock extends Block {

  @Override
  public String getDataBlock() {
    return "TextBlock";
  }

  public void setText(String text) {
    clearBlocks();

    var bufferedString = new BufferedString();
    int textPosition = 0;
    while (textPosition < text.length()) {
      var c = text.charAt(textPosition);
      if (c == ' ') {
        if (bufferedString.isNotEmpty()) {
          var charsBlock = new CharsBlock();
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
      var charsBlock = new CharsBlock();
      parentBlock.addBlock(charsBlock);
      charsBlock.type = CharsBlock.TYPE_CHARS;
      charsBlock.setChars(bufferedString.getString());
    }
  }

  public void setHtmlText(String htmlText) {
    clearBlocks();

    var styleJsonObjects = new ArrayList<JsonObject>();
    var htmlParser = new HtmlParser();
    htmlParser.input = htmlText;
    htmlParser.inputPosition = 0;
    htmlParser.parseHtmlTextContents(this, styleJsonObjects);
  }

  public void setMarkdownText(String markdownText) {
    clearBlocks();

    var styleJsonObjects = new ArrayList<JsonObject>();
    var markdownParser = new MarkdownParser();
    markdownParser.input = markdownText;
    markdownParser.inputPosition = 0;
    markdownParser.parseMarkdownTextContents(this, styleJsonObjects);
  }

  public String getText() {
    if (getBlocks() == null) {
      return "";
    }
    var bufferedString = new BufferedString();
    for (Block block : getBlocks()) {
      if (block instanceof CharsBlock) {
        bufferedString.appendString(((CharsBlock) block).getChars());
      }
    }
    return bufferedString.getString();
  }
}
