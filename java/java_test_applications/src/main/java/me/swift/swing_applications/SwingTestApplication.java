package me.swift.swing_applications;

import me.swift.engine.Page;
import me.swift.engine.test_components.TestView0;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SwingTestApplication extends JPanel {

  SwingDevice swingDevice = new SwingDevice(this);
  Page page = null;

  ScheduledExecutorService scheduledExecutorService = null;
  long lastTickTime = 0L;

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

  public void startRepainting() {
    if (scheduledExecutorService != null) {
      return;
    }
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    lastTickTime = swingDevice.getTime();
    scheduledExecutorService.scheduleAtFixedRate(this::tick, 0, 2, TimeUnit.MILLISECONDS);
  }

  private void tick() {
    long tickTime = swingDevice.getTime();
    if (tickTime - lastTickTime < 16) {
      return;
    }
    lastTickTime = tickTime;
    if (page.needsRepainting()) {
      SwingUtilities.invokeLater(() -> repaint());
    }
    if (!page.needsNextRepainting()) {
      scheduledExecutorService.shutdown();
      scheduledExecutorService = null;
    }
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
