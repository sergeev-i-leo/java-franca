package franca.java.graphics_applications;

import franca.java.RouterPage0;
import franca.java.graphics.GraphicsDevice;

import javax.swing.*;

public class GraphicsTestApplication {

  public static void main(String[] args) {
    GraphicsDevice graphicsDevice = new GraphicsDevice();
    graphicsDevice.routerPage = new RouterPage0(graphicsDevice);

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.add(graphicsDevice.getGraphicsPanel());

    frame.pack();
    frame.setSize(800, 800);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
