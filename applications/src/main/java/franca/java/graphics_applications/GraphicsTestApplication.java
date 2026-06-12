package franca.java.graphics_applications;

import franca.java.platforms.graphics.GraphicsApplication;
import franca.java.platforms.graphics.GraphicsRouter;

import javax.swing.*;
import java.franca.graphics.test_components.TestView0;
import java.franca.graphics.views.Page;

public class GraphicsTestApplication {

  public static void main(String[] args) {
    GraphicsRouter graphicsRouter = new GraphicsRouter();
    Page page = new Page(graphicsRouter);
    page.views.add(new TestView0());

    graphicsRouter.pushPage(page);

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GraphicsApplication swingApplication = new GraphicsApplication(graphicsRouter);
    frame.add(swingApplication);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
