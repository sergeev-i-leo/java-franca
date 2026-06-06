package franca.swift.step_gs.renderer;

import franca.swift.engine.data.json.JsonObject;
import franca.swift.step_gs.contract.Device;
import franca.swift.step_gs.painter.Painter;

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
