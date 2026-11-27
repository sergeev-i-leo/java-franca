package franca.java.ui.views.typography;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.View;

public class LettersView extends View {

  public String letters = null;
  public TextStyle textStyle = new TextStyle();

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, int zoomPercentage) {
    painter.paintText(letters, painterX + left, painterY + top, textStyle.fontName, textStyle.fontWeight, textStyle.fontStyle, textStyle.color, zoomPercentage);
  }
}
