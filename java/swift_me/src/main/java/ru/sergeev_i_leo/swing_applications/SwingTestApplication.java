package ru.sergeev_i_leo.swing_applications;

import ru.sergeev_i_leo.engine.Page;
import ru.sergeev_i_leo.engine.test_components.TestView0;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwingTestApplication extends JPanel {

  SwingDevice swingDevice = new SwingDevice(this);
  Page page = null;

  public SwingTestApplication() {
    setPreferredSize(new Dimension(800, 600));
    setBackground(Color.WHITE);

    page = new Page(swingDevice);
    page.views.add(new TestView0());

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
          page.handlePointerDown(mouseEvent.getX(), mouseEvent.getY(), 1);
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
          page.handlePointerDown(mouseEvent.getX(), mouseEvent.getY(), 3);
        }
      }
    });
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    page.paint(new SwingPainter(graphics));
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("SwingTestApplication");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    SwingTestApplication swingTestApplication = new SwingTestApplication();
    frame.add(swingTestApplication);

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
