package franca.java.ui;

import java.util.ArrayList;

public class Router {

  public Device device;
  public ArrayList<Page> pages = new ArrayList<>();

  public Router(Device device) {
    super();
    this.device = device;
  }

  public void paint(Painter painter) {
    if (pages.isEmpty()) {
      return;
    }
    pages.get(0).paint(painter);
  }

  public void didPainted() {
    if (pages.isEmpty()) {
      return;
    }
    pages.get(0).didPainted();
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (pages.isEmpty()) {
      return;
    }

  }
}
