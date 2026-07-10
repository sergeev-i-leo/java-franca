package franca.java.graphics;

import franca.java.expected.TranspilableClass;
import franca.java.expected.Painter;
import franca.java.expected.Router;

import java.util.ArrayList;

public class Page extends TranspilableClass {

  Router router = null;

  public ArrayList<View> views = new ArrayList<>();

  public Page nextPage = null;

  public Page(Router router) {
    super();

    this.router = router;
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
