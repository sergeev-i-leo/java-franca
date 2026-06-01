package me.swift.swing_applications;

import me.swift.step_gs.Page;
import me.swift.step_gs.test_components.HtmlParserView;

import javax.swing.*;

public class HtmlParserApplication {

  public static void main(String[] args) {
    Page page = new Page();
    page.views.append(new HtmlParserView());

    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    SwingApplication swingApplication = new SwingApplication(page);
    frame.add(swingApplication);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
