package franca.swift.swing_applications;

import franca.swift.step_gs.renderer.Page;
import franca.swift.step_gs.test_components.FlavouredMarkdownParserView;

import javax.swing.*;

public class FlavouredMarkdownParserApplication {

  public static void main(String[] args) {
    Page page = new Page();
    page.views.append(new FlavouredMarkdownParserView());

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    SwingApplication swingApplication = new SwingApplication(page);
    frame.add(swingApplication);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
