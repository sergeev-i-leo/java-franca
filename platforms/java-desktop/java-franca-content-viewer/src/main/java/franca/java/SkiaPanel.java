package franca.java;

import javax.swing.*;
import java.awt.*;

public class SkiaPanel extends JPanel {

  public SkiaPanel() {
    setBackground(Color.WHITE);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2d.setColor(Color.BLUE);
    g2d.drawString("Skia View (заглушка)", 50, 50);
    g2d.drawRect(50, 70, 200, 100);
    g2d.setColor(Color.RED);
    g2d.fillOval(300, 70, 100, 100);
  }
}
