package franca.java.graphics;

import franca.java.graphics.device.Painter;

import java.awt.*;

public class GraphicsPainter extends Painter {

  Graphics graphics;

  public GraphicsPainter(Graphics graphics) {
    super();
    this.graphics = graphics;
  }

  @Override
  public void paintText(String text, float x, float y, String deviceFontKey, int deviceColor) {
    graphics.setColor(Color.BLACK);
    graphics.setFont(new Font("Arial", Font.BOLD, 48));
    graphics.drawString(text, (int) x, (int) y);
  }
}
