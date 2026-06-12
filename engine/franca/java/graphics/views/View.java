package franca.java.graphics.views;

import franca.java.parsers.json.JsonObject;
import franca.java.graphics.device.Painter;
import franca.java.graphics.device.Router;

public class View extends JsonObject {

  @Override
  public void destroy() {
    super.destroy();
  }

  public void paint(Router router, Painter painter, Page page) {
  }

  public void handlePointerDown(Router router, Page page, float painterX, float painterY, float pointedX, float pointedY, int buttonNumber) {
  }
}
