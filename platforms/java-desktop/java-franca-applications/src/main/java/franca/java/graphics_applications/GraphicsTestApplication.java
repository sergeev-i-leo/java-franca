package franca.java.graphics_applications;

import franca.java.graphics.GraphicsApplication;
import franca.java.graphics.GraphicsRouter;

import javax.swing.*;

import franca.java.graphics.Page;
import franca.java.test_components.TestView0;

public class GraphicsTestApplication {

  public static void main(String[] args) {
    GraphicsRouter graphicsRouter = new GraphicsRouter();
    Page page = new Page(graphicsRouter);
    page.views.add(new TestView0());

    graphicsRouter.pushPage(page);

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GraphicsApplication graphicsApplication = new GraphicsApplication(graphicsRouter);
    frame.add(graphicsApplication);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
