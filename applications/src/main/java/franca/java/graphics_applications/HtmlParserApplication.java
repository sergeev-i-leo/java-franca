package franca.java.graphics_applications;

import franca.java.platforms.graphics.GraphicsApplication;
import franca.java.platforms.graphics.GraphicsRouter;

import javax.swing.*;
import java.franca.graphics.test_components.HtmlParserView;
import java.franca.graphics.views.Page;

public class HtmlParserApplication {

  public static void main(String[] args) {
    GraphicsRouter graphicsRouter = new GraphicsRouter();
    Page page = new Page(graphicsRouter);
    page.views.add(new HtmlParserView());

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
