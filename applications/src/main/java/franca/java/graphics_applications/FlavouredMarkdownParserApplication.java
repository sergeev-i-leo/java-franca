package franca.java.graphics_applications;

import franca.java.platforms.graphics.GraphicsApplication;
import franca.java.platforms.graphics.GraphicsRouter;

import javax.swing.*;
import java.franca.graphics.test_components.FlavouredMarkdownParserView;
import java.franca.graphics.views.Page;

public class FlavouredMarkdownParserApplication {

  public static void main(String[] args) {
    GraphicsRouter graphicsRouter = new GraphicsRouter();
    Page page = new Page(graphicsRouter);
    page.views.add(new FlavouredMarkdownParserView());

    graphicsRouter.pushPage(page);

    JFrame frame = new JFrame("GraphicsTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GraphicsApplication graphicsApplication = new GraphicsApplication(graphicsRouter);
    frame.add(graphicsApplication);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
