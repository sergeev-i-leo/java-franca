package franca.java.graphics_applications;

import franca.java.TestView0;
import franca.java.graphics.GraphicsRouter;

import javax.swing.*;

import franca.java.graphics.Page;

public class GraphicsTestApplication {

  public static void main(String[] args) {
    GraphicsRouter graphicsRouter = new GraphicsRouter();
    Page page = new Page(graphicsRouter);
    page.views.add(new TestView0());

    graphicsRouter.pushPage(page);

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.add(graphicsRouter.graphicsPanel);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
