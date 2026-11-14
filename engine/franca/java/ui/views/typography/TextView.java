package franca.java.ui.views.typography;

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
    setReadiness(-1);
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    if (isReady()) {
      return;
    }
    if (getReadiness() == 0) {
      return;
    }
    setReadiness(0);

    lettersViews.clear();

    width = 0;
    height = 0;

    if (textBlock == null) {
      setReadiness(1);
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
      formatItem.fontHeight = painter.calculateFontHeight(formatItem.fontName, formatItem.fontWeight, formatItem.fontStyle);
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

      setReadiness(1);
      return;
    }

    availableWidth = availableWidth - paddingLeft - paddingRight;
    height = paddingTop;
    availableWidth=400;

    // first letter
    int i0 = 0;
    // space
    int i1 = 0;
    int accumulatedWidth = 0;
    int i2 = 0;
    while (i2 < formatItems.size()) {
      FormatItem formatItem = formatItems.get(i2);
      if (!formatItem.lettersBlock.type.equals(LettersBlock.TYPE_SPACE)) {
        accumulatedWidth += formatItem.width;
        i2++;
        continue;
      }
      // space found
      if (accumulatedWidth + formatItem.width < availableWidth) {
        i1 = i2;
        accumulatedWidth += formatItem.width;
        i2++;
        continue;
      }
      if (i1 == i0) {
        // first space on long line
        buildLettersViews(formatItems, i0, i2);
        i0 = i2 + 1;
        i1 = i2 + 1;
        accumulatedWidth = 0;
        i2++;
        continue;
      }
      buildLettersViews(formatItems, i0, i1);
      i0 = i1 + 1;
      i1 = i2;
      accumulatedWidth = 0;
      for (int i3 = i0; i3 <= i1; i3++) {
        accumulatedWidth += formatItems.get(i3).width;
      }
      i2++;
    }

    i2--;
    if (i0 <= i2) {
      buildLettersViews(formatItems, i0, i2);
    }

    height += paddingBottom;

    setReadiness(1);
  }

  public void buildLettersViews(ArrayList<FormatItem> formatItems, int i0, int i1) {
    FormatItem formatItem = formatItems.get(i0);
    String fontName = formatItem.fontName;
    String fontWeight = formatItem.fontWeight;
    String fontStyle = formatItem.fontStyle;
    int color = formatItem.color;
    int fontHeight = formatItem.fontHeight;

    if (!lettersViews.isEmpty()) {
      height += lineSpacing;
    }

    int x = paddingLeft;
    int allocatedWidth = 0;
    int lineHeight = 0;

    BufferedString bufferedString = new BufferedString();
    for (int i2 = i0; i2 <= i1; i2++) {
      formatItem = formatItems.get(i2);
      boolean formatChanged = false;
      if ((formatItem.fontName != null) && (fontName != null)) {
        if (!formatItem.fontName.equals(fontName)) {
          formatChanged = true;
        }
      } else if ((formatItem.fontName != null) || (fontName != null)) {
        formatChanged = true;
      }
      if ((formatItem.fontWeight != null) && (fontWeight != null)) {
        if (!formatItem.fontWeight.equals(fontWeight)) {
          formatChanged = true;
        }
      } else if ((formatItem.fontWeight != null) || (fontWeight != null)) {
        formatChanged = true;
      }
      if ((formatItem.fontStyle != null) && (fontStyle != null)) {
        if (!formatItem.fontStyle.equals(fontStyle)) {
          formatChanged = true;
        }
      } else if ((formatItem.fontStyle != null) || (fontStyle != null)) {
        formatChanged = true;
      }
      if (formatItem.color != color) {
        formatChanged = true;
      }

      if (!formatChanged) {
        bufferedString.appendString(formatItem.lettersBlock.letters);
        allocatedWidth += formatItem.width;
        continue;
      }
      LettersView lettersView = new LettersView();
      lettersViews.add(lettersView);
      lettersView.letters = bufferedString.getString();
      lettersView.left = x;
      lettersView.top = height;
      lettersView.width = allocatedWidth;
      lettersView.height = fontHeight;

      lettersView.textStyle = new TextStyle();
      lettersView.textStyle.fontName = fontName;
      lettersView.textStyle.fontWeight = fontWeight;
      lettersView.textStyle.fontStyle = fontStyle;
      lettersView.textStyle.color = color;

      x += allocatedWidth;
      lineHeight = Math.max(fontHeight, lineHeight);

      bufferedString.clear();
      bufferedString.appendString(formatItem.lettersBlock.letters);

      fontName = formatItem.fontName;
      fontWeight = formatItem.fontWeight;
      fontStyle = formatItem.fontStyle;
      color = formatItem.color;
      fontHeight = formatItem.fontHeight;
    }

    if (bufferedString.isNotEmpty()) {
      LettersView lettersView = new LettersView();
      lettersViews.add(lettersView);
      lettersView.letters = bufferedString.getString();
      lettersView.textStyle = new TextStyle();
      lettersView.left = x;
      lettersView.top = height;
      lettersView.width = allocatedWidth;
      lettersView.height = fontHeight;

      lineHeight = Math.max(fontHeight, lineHeight);
    }

    height += lineHeight;
  }

  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    painter.setCurrentTextStyle(textStyle);
    for (int i = 0; i < lettersViews.size(); i++) {
      LettersView letterView = lettersViews.get(i);
      letterView.paint(device, painter, painterX, painterY, scale);
    }
  }
}

class FormatItem extends TextStyle {

  LettersBlock lettersBlock;
  int width = 0;
  int fontHeight = 0;

  FormatItem(LettersBlock lettersBlock) {
    this.lettersBlock = lettersBlock;
  }
}
