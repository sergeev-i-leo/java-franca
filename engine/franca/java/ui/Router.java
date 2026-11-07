package franca.java.ui;

import franca.java.ui.views.View;

import java.util.ArrayList;

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
    if (view.isReady()) {
      view.paint(device, painter);
      return;
    }
    view.getReady(device, painter, device.getWidth(), device.getHeight(), 1f);
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (views.isEmpty()) {
      return;
    }

  }
}
