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
import java.util.Objects;

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
      formatItem.fontWeight = lettersBlock.styleJsonObject.getStringValue("font-weight");
      if (formatItem.fontWeight == null) {
        formatItem.fontWeight = textStyle.fontWeight;
      }
      formatItem.fontStyle = lettersBlock.flavouredStyleJsonObject.getStringValue("font-style");
      if (formatItem.fontStyle == null) {
        formatItem.fontStyle = textStyle.fontStyle;
      }
      formatItem.width = painter.calculateTextWidth(lettersBlock.letters, formatItem.fontName, formatItem.fontWeight, formatItem.fontStyle);
    }

    lettersViews.clear();

    if (formatItems.isEmpty()) {
      LettersView lettersView = new LettersView();
      lettersViews.add(lettersView);
      lettersView.left = paddingLeft;
      lettersView.top = paddingTop;
      lettersView.letters = "";
      lettersView.height = painter.calculateFontHeight(textStyle.fontName, textStyle.fontWeight, textStyle.fontStyle);

      width = paddingLeft + lettersView.width + paddingRight;
      height = paddingTop + lettersView.height + paddingBottom;

      setState(1);
      return;
    }

    availableWidth = availableWidth - paddingLeft - paddingRight;
    int viewsTop = paddingTop;
    availableWidth=4000;

    // first letter
    int i0 = 0;
    // space
    int i1 = 0;
    int viewsWidth = 0;
    int i2 = 0;
    while (i2 < formatItems.size()) {
      FormatItem formatItem = formatItems.get(i2);
      if (!formatItem.lettersBlock.letters.equals(" ")) {
        viewsWidth += formatItem.width;
        i2++;
        continue;
      }
      // space found
      if (viewsWidth + formatItem.width < availableWidth) {
        i1 = i2;
        viewsWidth += formatItem.width;
        i2++;
        continue;
      }
      if (i1 == i0) {
        // first space on long line
        viewsTop += buildLettersViews(formatItems, i0, i2, viewsTop);
        viewsTop += lineSpacing;
        i0 = i2 + 1;
        i1 = i2 + 1;
        viewsWidth = 0;
        i2++;
        continue;
      }
      viewsTop += buildLettersViews(formatItems, i0, i1, viewsTop);
      viewsTop += lineSpacing;
      i0 = i1 + 1;
      i1 = i2;
      viewsWidth = 0;
      for (int i3 = i0; i3 <= i1; i3++) {
        viewsWidth += formatItems.get(i3).width;
      }
      i2++;
    }

    setState(1);
  }

  public int buildLettersViews(ArrayList<FormatItem> formatItems, int i0, int i1, int viewsTop) {
    int lineHeight = 0;
    /*BufferedString bufferedString = new BufferedString();
    String key0 = null;
    if (key0 == null) {
      key0 = formatItem.getKey();
    } else {
      String key1 = formatItem.getKey();
    }*/

    return lineHeight;
  }

  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    painter.setCurrentTextStyle(textStyle);
  }
}

class FormatItem extends TextStyle {

  LettersBlock lettersBlock;
  int width = 0;

  FormatItem(LettersBlock lettersBlock) {
    this.lettersBlock = lettersBlock;
  }

  String getKey() {
    String key;

    if (fontName != null) {
      key = fontName;
    } else {
      key = "fontName";
    }

    if (fontWeight != null) {
      key += "#" + fontWeight;
    } else {
      key += "$fontWeight";
    }

    if (fontStyle != null) {
      key += "#" + fontStyle;
    } else {
      key += "#fontStyle";
    }

    return key + "#" + color;
  }
}
