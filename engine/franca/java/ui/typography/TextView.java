package franca.java.ui.typography;

import franca.java.data.markdown.MarkdownParser;
import franca.java.office.document.Block;
import franca.java.office.document.typography.HeadingBlock;
import franca.java.office.document.typography.TextBlock;
import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.View;

import java.util.ArrayList;

public class TextView extends View {

  public String fontName = null;
  public Integer fontColor = null;

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

    left = 0;
    top = 0;
    width = 0;
    height = 0;

    if (textBlock == null) {
      setState(1);
      return;
    }

    if (textBlock instanceof HeadingBlock) {
      HeadingBlock headingBlock = (HeadingBlock) textBlock;
      fontName = "secondary-h" + headingBlock.level;
    } else {
      fontName = "primary";
    }

    fontColor = Painter.PRIMARY_TEXT_COLOR;
    lettersViews.clear();

    LettersView lettersView = null;
    boolean bold = false;
    boolean italic = false;

    ArrayList<Block> blocks = textBlock.getInnerBlocks();

    for (int i = 0; i < blocks.size(); i++) {
      Block block = blocks.get(i);

    }

    setState(1);
  }
}
