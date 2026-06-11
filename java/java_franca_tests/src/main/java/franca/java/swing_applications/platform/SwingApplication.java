package franca.java.swing_applications.platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import franca.java.platform.JavaDevice;

public class SwingApplication extends JPanel {

  JavaDevice javaDevice = new JavaDevice();
  SwingRouter swingRouter;

  ScheduledExecutorService scheduledExecutorService = null;
  long lastTickTime = 0L;

  public SwingApplication(SwingRouter swingRouter) {
    super();

    this.swingRouter = swingRouter;
    swingRouter.swingApplication = this;
    swingRouter.setDevice(javaDevice);

    setPreferredSize(new Dimension(800, 600));
    setBackground(Color.WHITE);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
          swingRouter.handlePointerDown(mouseEvent.getX(), mouseEvent.getY(), 1);
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
          swingRouter.handlePointerDown(mouseEvent.getX(), mouseEvent.getY(), 3);
        }
      }
    });
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    swingRouter.paint(new SwingPainter(graphics));
  }

  public void startRepainting() {
    if (scheduledExecutorService != null) {
      return;
    }
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    lastTickTime = javaDevice.getTime();
    scheduledExecutorService.scheduleAtFixedRate(this::tick, 0, 2, TimeUnit.MILLISECONDS);
  }

  private void tick() {
    long tickTime = javaDevice.getTime();
    if (tickTime - lastTickTime < 16) {
      return;
    }
    lastTickTime = tickTime;
    if (swingRouter.needsRepainting()) {
      SwingUtilities.invokeLater(() -> repaint());
    }
    if (!swingRouter.needsNextRepainting()) {
      scheduledExecutorService.shutdown();
      scheduledExecutorService = null;
    }
  }
}
