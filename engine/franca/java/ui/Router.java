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
    view.getReady(device, painter, device.getWidth(), device.getHeight(), 1f);
    // repaint
    device.requestPainting();
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (views.isEmpty()) {
      return;
    }

  }
}
