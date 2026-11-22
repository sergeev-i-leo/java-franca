package franca.java.graphics_applications;

import javax.swing.*;

import franca.java.Graphics2DDevice;
import franca.java.Graphics2DPanel;
import franca.java.Router0;

public class GraphicsTestApplication {

  public static void main(String[] args) {
    Graphics2DDevice graphics2DDevice = new Graphics2DDevice();
    graphics2DDevice.router = new Router0(graphics2DDevice);

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    graphics2DDevice.graphics2DPanel = new Graphics2DPanel(graphics2DDevice);
    frame.add(graphics2DDevice.graphics2DPanel);

    frame.pack();
    frame.setSize(800, 800);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
