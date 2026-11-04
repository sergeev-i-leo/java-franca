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
    topPage.didPainted();
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {

  }
}
