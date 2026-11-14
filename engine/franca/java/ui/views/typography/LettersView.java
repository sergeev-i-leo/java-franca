package franca.java.ui.views.typography;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.TextStyle;
import franca.java.ui.views.View;

public class LettersView extends View {

  public String letters = null;

  public LettersView() {
    super();
    textStyle = new TextStyle();
  }

  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    painter.paintText(letters, painterX + left, painterY + top, textStyle.fontName, textStyle.fontWeight, textStyle.fontStyle, textStyle.color, scale);
  }
}
