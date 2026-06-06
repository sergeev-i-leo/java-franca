package franca.java.graphics.renderer;

import franca.java.transpiler.data.json.JsonObject;
import franca.java.graphics.contract.Device;
import franca.java.graphics.painter.Painter;

public class View extends JsonObject {

  @Override
  public void destroy() {
    super.destroy();
  }

  public void paint(Device device, Painter painter, Page page) {
  }

  public void handlePointerDown(Device device, Page page, float painterX, float painterY, float pointedX, float pointedY, int buttonNumber) {
  }
}
