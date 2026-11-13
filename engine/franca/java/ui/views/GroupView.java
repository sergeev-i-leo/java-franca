package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Painter;

import java.util.ArrayList;

public class GroupView extends View {

  public ArrayList<View> views = new ArrayList<>();

  public Float viewsScale = null;

  public boolean isReady() {
    int state = getState();
    if (state != 1) {
      return false;
    }
    for (int i = 0; i < views.size(); i++) {
      if (!views.get(i).isReady()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    if (isReady()) {
      return;
    }
    if (getState() == 0) {
      return;
    }
    setState(0);

    if (viewsScale != null) {
      scale *= viewsScale;
    }

    width = 0;
    height = 0;

    int viewsTop = 0;

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);

      if (view.zIndex == 0) {
        view.left = 0;
        view.top = viewsTop;
      }

      view.getReady(device, painter, availableWidth, availableHeight, scale);

      if (view.zIndex > 0) {
        continue;
      }

      width = Math.max(view.left + view.width, width);
      height = Math.max(view.top + view.height, height);

      viewsTop += view.height;
    }

    setState(1);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).paint(device, painter, painterX, painterY, scale);
    }
  }
}
