package franca.java.ui.typography;

import franca.java.office.document.typography.TextBlock;
import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.View;

import java.util.ArrayList;

public class LabelView extends View {

  public String text = "";

  public String fontName = null;
  public Integer fontColor = null;
  public Integer fontColorOpacity = null;
  public boolean isMultiline = true;

  public TextBlock textBlock = null;

  public ArrayList<CharsView> charsViews = new ArrayList<>();

  public void setText(String text) {
    textBlock = new TextBlock();
    textBlock.setText(text);
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

    charsViews.clear();

    left = 0;
    top = 0;
    width = 0;
    height = 0;

    setState(1);
  }
}
