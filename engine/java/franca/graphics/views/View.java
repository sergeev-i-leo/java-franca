package java.franca.graphics.views;

import java.franca.parsers.json.JsonObject;
import java.franca.graphics.device.Painter;
import java.franca.graphics.device.Router;

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
