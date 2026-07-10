package franca.java.graphics;

import franca.java.common.JavaDevice;
import franca.java.graphics.device.Router;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GraphicsRouter extends Router {

  public GraphicsPanel graphicsPanel = new GraphicsPanel(this);

  ScheduledExecutorService scheduledExecutorService = null;
  long lastTickTime = 0L;

  public GraphicsRouter() {
    super();
    device = new JavaDevice();
  }

  public void startRepainting() {
    if (scheduledExecutorService != null) {
      return;
    }
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    lastTickTime = device.getTime();
    scheduledExecutorService.scheduleAtFixedRate(this::tick, 0, 2, TimeUnit.MILLISECONDS);
  }

  private void tick() {
    long tickTime = device.getTime();
    if (tickTime - lastTickTime < 16) {
      return;
    }
    lastTickTime = tickTime;
    if (needsRepainting()) {
      SwingUtilities.invokeLater(() -> graphicsPanel.repaint());
    }
    if (!needsNextRepainting()) {
      scheduledExecutorService.shutdown();
      scheduledExecutorService = null;
    }
  }
}

class GraphicsPanel extends JPanel {

  GraphicsRouter graphicsRouter;

  GraphicsPanel(GraphicsRouter graphicsRouter) {
    super();
    this.graphicsRouter = graphicsRouter;

    setPreferredSize(new Dimension(800, 600));
    setBackground(Color.WHITE);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
          graphicsRouter.handlePointerDown(mouseEvent.getX(), mouseEvent.getY(), 1);
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
          graphicsRouter.handlePointerDown(mouseEvent.getX(), mouseEvent.getY(), 3);
        }
      }
    });
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    graphicsRouter.paint(new GraphicsPainter(graphics));
  }
}
