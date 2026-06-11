package franca.java.swing_applications;

import franca.java.graphics.views.Page;
import franca.java.graphics.test_components.FlavouredMarkdownParserView;
import franca.java.swing_applications.platform.SwingApplication;
import franca.java.swing_applications.platform.SwingRouter;

import javax.swing.*;

public class FlavouredMarkdownParserApplication {

  public static void main(String[] args) {
    SwingRouter swingRouter = new SwingRouter();
    Page page = new Page(swingRouter);
    page.views.add(new FlavouredMarkdownParserView());

    swingRouter.pushPage(page);

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    SwingApplication swingApplication = new SwingApplication(swingRouter);
    frame.add(swingApplication);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
