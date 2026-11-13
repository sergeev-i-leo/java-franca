package franca.java.ui;

import java.util.ArrayList;

import franca.java.ui.views.View;

public class Router {

  public Device device;
  public ArrayList<View> views = new ArrayList<>();

  public Router(Device device) {
    super();
    this.device = device;
  }

  public void paint(Painter painter) {
    if (views.isEmpty()) {
      return;
    }
    View view = views.get(0);
    view.left = 0;
    view.top = 0;
    if (view.isReady()) {
      view.paint(device, painter, view.left, view.top, 1f);
      return;
    }
    long startTime = System.nanoTime();
    view.getReady(device, painter, painter.getWidth(), painter.getHeight(), 1f);
    long endTime = System.nanoTime();
    long durationInNanos = endTime - startTime;
    long durationInMillis = durationInNanos / 1_000_000;
    System.out.println("get ready за: " + durationInMillis + " мс");
    // repaint
    device.requestPainting();
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (views.isEmpty()) {
      return;
    }

  }
}
