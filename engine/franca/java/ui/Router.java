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
    views.get(0).paint(device, painter);
  }

  public void didPainted() {
    if (views.isEmpty()) {
      return;
    }
    views.get(0).didPainted(device);
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (views.isEmpty()) {
      return;
    }

  }
}
