package franca.java.graphics.views;

import contracted.franca.java.TranspilableClass;
import contracted.franca.java.ContractedArray;
import franca.java.graphics.device.Painter;
import franca.java.graphics.device.Router;

public class Page extends TranspilableClass {

  Router router = null;

  public ContractedArray<View> views = new ContractedArray<View>();

  public Page nextPage = null;

  public Page(Router router) {
    this.router = router;
  }

  @Override
  public void destroy() {
    delete(views);
    views = null;
    super.destroy();
  }

  public void paint(Painter painter) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).paint(router, painter, this);
    }
  }

  public void handlePointerDown(float pointedX, float pointedY, int buttonNumber) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).handlePointerDown(router, this, 0f, 0f, pointedX, pointedY, buttonNumber);
    }
  }
}
