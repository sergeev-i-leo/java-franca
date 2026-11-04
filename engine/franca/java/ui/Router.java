package franca.java.ui;

public class Router {

  public Device device;
  public Page topPage = null;

  public Router(Device device) {
    super();
    this.device = device;
  }

  public void paint(Painter painter) {
    if (topPage != null) {
      topPage.paint(painter);
    }
  }

  public void didPainted() {
    if (topPage == null) {
      return;
    }
    for (int i = 0; i < topPage.views.size(); i++) {
      callDidPainted(topPage, topPage.views.get(i));
    }
  }

  public void callDidPainted(Page page, View view) {
    for (int i = 0; i < view.views.size(); i++) {
      callDidPainted(page, view.views.get(i));
    }
    view.didPainted(device, page);
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {

  }
}
