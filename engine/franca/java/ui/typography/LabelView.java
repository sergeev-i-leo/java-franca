package franca.java.ui.typography;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.View;

import java.util.ArrayList;

public class LabelView extends View {

  public String text = "";

  public String fontName = null;
  public Integer fontColor = null;
  public Integer fontColorOpacity = null;

  public ArrayList<LettersView> lettersViews = new ArrayList<>();

  public void setText(String text) {


  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    left = 0;
    top = 0;
    width = 0;
    height = 0;

    setState(1);
  }
}
