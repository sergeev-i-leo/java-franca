package franca.java.ui;

import franca.java.TranspilableClass;

import java.util.ArrayList;

public class Page extends TranspilableClass {

  public Device device = null;

  public ArrayList<View> views = new ArrayList<>();

  public Page nextPage = null;

  public Page(Device device) {
    super();

    this.device = device;
  }

  public void paint(Painter painter) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).paint(device, painter, this);
    }
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).handlePointerDown(device, this, 0, 0, pointedX, pointedY, buttonNumber);
    }
  }
}
