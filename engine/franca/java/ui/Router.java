package franca.java.ui;

import franca.java.ui.views.GroupView;
import franca.java.ui.views.View;

import java.util.ArrayList;

public class Router {

  public Device device;
  public ArrayList<View> views = new ArrayList<>();

  public Router(Device device) {
    super();
    this.device = device;
  }

  public void paint(Painter painter) {
    if (views.isEmpty()) {
      return;
    }
    views.get(0).paint(device, painter);
  }

  public void didPainted() {
    if (views.isEmpty()) {
      return;
    }
    callDidPainted(device, views.get(0));
  }

  public void callDidPainted(Device device, View view) {
    if (view instanceof GroupView) {
      GroupView groupView = (GroupView) view;
      for (int i = 0; i < groupView.views.size(); i++) {
        callDidPainted(device, groupView.views.get(i));
      }
    }
    view.didPainted(device);
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (views.isEmpty()) {
      return;
    }

  }
}
