package franca.java.ui.typography;

import franca.java.data.markdown.MarkdownParser;
import franca.java.expected.BufferedString;
import franca.java.office.document.Block;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.LettersBlock;
import franca.java.office.document.typography.TextBlock;
import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.View;

import java.util.ArrayList;

public class TextView extends View {

  public String fontName = null;
  public Integer fontColor = null;

  public int paddingLeft = 0;
  public int paddingTop = 0;
  public int paddingRight = 0;
  public int paddingBottom = 0;
  public int lineSpacing = 0;

  public TextBlock textBlock = null;

  public ArrayList<LettersView> lettersViews = new ArrayList<>();

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

    fontName = textBlock.flavouredStyleJsonObject.getStringValue("font-name");
    fontColor = textBlock.flavouredStyleJsonObject.getIntegerValue("font-color");
    if (fontColor == null) {
      fontColor = Painter.PRIMARY_TEXT_COLOR;
    }

    lettersViews.clear();

    LettersView lettersView = null;
    boolean bold = false;
    boolean italic = false;

    ArrayList<Block> blocks = textBlock.getInnerBlocks();

    BufferedString bufferedString = new BufferedString();

    int x = paddingLeft;
    int y = paddingTop;
    availableWidth = availableWidth - paddingLeft - paddingRight;

    ArrayList<Integer> widths = new ArrayList<>();

    for (int i = 0; i < blocks.size(); i++) {
      Block block = blocks.get(i);
      if (!(block instanceof LettersBlock)) {
        widths.add(0);
        continue;
      }
      LettersBlock lettersBlock = (LettersBlock) block;
      String lettersBlockFontName = lettersBlock.flavouredStyleJsonObject.getStringValue("font-name");
      if (lettersBlockFontName == null) {
        int width = painter.calculateTextWidth(fontName, lettersBlock.letters);
        widths.add(width);
      }
    }

    if (lettersView == null) {
      lettersView = new LettersView();
      lettersViews.add(lettersView);
      lettersView.fontName = fontName;
      lettersView.fontColor = fontColor;
      lettersView.left = paddingLeft;
      lettersView.top = paddingTop;
      lettersView.getReady(device, painter, availableWidth, availableHeight, scale);
      width = lettersView.left + lettersView.width + paddingRight;
      height = lettersView.top + lettersView.height + paddingBottom;
    }

    setState(1);
  }

  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
  }
}
