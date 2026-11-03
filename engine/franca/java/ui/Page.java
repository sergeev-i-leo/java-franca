package franca.java.ui;

import franca.java.TranspilableClass;

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

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).handlePointerDown(router, this, 0, 0, pointedX, pointedY, buttonNumber);
    }
  }
}
