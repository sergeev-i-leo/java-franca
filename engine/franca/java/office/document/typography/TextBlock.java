package franca.java.office.document.typography;

import franca.java.data.html.HtmlEngine;
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

  @Override
  public void fillJsonObject(JsonObject jsonObject) {
    super.fillJsonObject(jsonObject);
    jsonObject.putStringValue("text", getText());
  }

  @Override
  public void contentsToHtml(BufferedString targetBufferedString, String htmlTag, int spacesBefore) {
    super.contentsToHtml(targetBufferedString, htmlTag, -1);
    if (spacesBefore >= 0) {
      targetBufferedString.finishLine();
    }
  }

  public void setText(String text) {
    clearInnerBlocks();

    BufferedString bufferedString = new BufferedString();
    int textPosition = 0;
    while (textPosition < text.length()) {
      char c = text.charAt(textPosition);
      if (c == ' ') {
        if (bufferedString.isNotEmpty()) {
          LettersBlock lettersBlock = new LettersBlock();
          outerBlock.addInnerBlock(lettersBlock);
          lettersBlock.type = LettersBlock.TYPE_LETTERS;
          lettersBlock.setLetters(bufferedString.getString());
          bufferedString.clear();
        }
        LettersBlock lettersBlock = new LettersBlock();
        outerBlock.addInnerBlock(lettersBlock);
        lettersBlock.type = LettersBlock.TYPE_SPACE;
        lettersBlock.setLetters(" ");
      }
      textPosition++;
    }
    if (bufferedString.isNotEmpty()) {
      LettersBlock lettersBlock = new LettersBlock();
      outerBlock.addInnerBlock(lettersBlock);
      lettersBlock.type = LettersBlock.TYPE_LETTERS;
      lettersBlock.setLetters(bufferedString.getString());
    }
  }

  public void setHtmlText(String htmlText) {
    clearInnerBlocks();

    ArrayList styleJsonObjects = new ArrayList<JsonObject>();
    HtmlEngine htmlEngine = new HtmlEngine();
    htmlEngine.input = htmlText;
    htmlEngine.inputPosition = 0;
    htmlEngine.parseHtmlTextContents(this, styleJsonObjects);
  }

  public void setMarkdownText(String markdownText) {
    clearInnerBlocks();

    MarkdownParser markdownParser = new MarkdownParser();
    markdownParser.input = markdownText;
    markdownParser.inputPosition = 0;
    markdownParser.parseMarkdownTextContents(this, false);
  }

  public String getText() {
    if (getInnerBlocks() == null) {
      return "";
    }
    BufferedString bufferedString = new BufferedString();
    for (Block block : getInnerBlocks()) {
      if (block instanceof LettersBlock) {
        bufferedString.appendString(((LettersBlock) block).getLetters());
      }
    }
    return bufferedString.getString();
  }
}
