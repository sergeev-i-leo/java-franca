package franca.java.ui.typography;

import franca.java.data.markdown.MarkdownParser;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.typography.LettersBlock;
import franca.java.office.document.typography.TextBlock;
import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.View;
import franca.java.ui.TextStyle;

import java.util.ArrayList;

public class TextView extends View {

  public int paddingLeft = 0;
  public int paddingTop = 0;
  public int paddingRight = 0;
  public int paddingBottom = 0;
  public int lineSpacing = 0;

  public TextBlock textBlock = null;

  public ArrayList<LettersView> lettersViews = new ArrayList<>();

  public TextView() {
    super();
    textStyle = new TextStyle();
  }

  public void setText(String text) {
    MarkdownParser markdownParser = new MarkdownParser();
    textBlock = markdownParser.parseMarkdownTextBlock(text);
    setState(-1);
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    if (isReady()) {
      return;
    }
    if (getState() == 0) {
      return;
    }
    setState(0);

    lettersViews.clear();

    width = 0;
    height = 0;

    if (textBlock == null) {
      setState(1);
      return;
    }

    textStyle.fontName = textBlock.flavouredStyleJsonObject.getStringValue("font-name");

    ArrayList<Block> blocks = textBlock.getInnerBlocks();

    ArrayList<FormatItem> formatItems = new ArrayList<>();

    for (int i = 0; i < blocks.size(); i++) {
      Block block = blocks.get(i);
      if (!(block instanceof LettersBlock)) {
        continue;
      }

      LettersBlock lettersBlock = (LettersBlock) block;
      FormatItem formatItem = new FormatItem(lettersBlock);
      formatItems.add(formatItem);

      formatItem.fontName = lettersBlock.flavouredStyleJsonObject.getStringValue("font-name");
      if (formatItem.fontName == null) {
        formatItem.fontName = textStyle.fontName;
      }
      formatItem.fontWeight = lettersBlock.styleJsonObject.getStringValue("font-wight");
      if (formatItem.fontWeight == null) {
        formatItem.fontWeight = textStyle.fontWeight;
      }
      formatItem.fontStyle = lettersBlock.flavouredStyleJsonObject.getStringValue("font-style");
      if (formatItem.fontStyle == null) {
        formatItem.fontStyle = textStyle.fontStyle;
      }
      formatItem.width = painter.calculateTextWidth(lettersBlock.letters, formatItem.fontName, formatItem.fontWeight, formatItem.fontStyle);
      formatItem.buildKey();
    }

    lettersViews.clear();

    if (formatItems.isEmpty()) {
      LettersView lettersView = new LettersView();
      lettersViews.add(lettersView);

      FormatItem formatItem = new FormatItem(null);
      formatItems.add(formatItem);
      formatItem.fontName = textStyle.fontName;
      formatItem.fontWeight = textStyle.fontWeight;
      formatItem.fontStyle = textStyle.fontStyle;
    }

    BufferedString bufferedString = new BufferedString();

    int x = paddingLeft;
    int y = paddingTop;
    availableWidth = availableWidth - paddingLeft - paddingRight;

    LettersView lettersView = null;
    String key = "";
    for (int i = 0; i < formatItems.size(); i++) {
      FormatItem formatItem = formatItems.get(i);
      if (formatItem.lettersBlock == null) {
        // empty
        lettersView = new LettersView();
        lettersViews.add(lettersView);
        lettersView.letters = "";
        lettersView.fontName = fontName;
        lettersView.fontStyle = fontStyle;
        lettersView.fontName = fontName;
        lettersView.color = fontName;
      } else {

      }
    }

    setState(1);
  }

  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    painter.setCurrentTextStyle(textStyle);
  }
}

class FormatItem {

  LettersBlock lettersBlock;
  String fontName = null;
  String fontWeight = null;
  String fontStyle = null;
  int color = 0;
  int width = 0;
  String key = null;

  FormatItem(LettersBlock lettersBlock) {
    this.lettersBlock = lettersBlock;
  }

  void buildKey() {

    if (fontName != null) {
      key = fontName;
    } else {
      key = "fontName";
    }

    if (fontWeight != null) {
      key += fontWeight;
    } else {
      key += "fontWeight";
    }

    if (fontStyle != null) {
      key += fontStyle;
    } else {
      key += "fontStyle";
    }
  }
}
