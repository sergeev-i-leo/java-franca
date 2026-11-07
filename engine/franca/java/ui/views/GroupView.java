package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Painter;

import java.util.ArrayList;

public class GroupView extends View {

  public ArrayList<View> views = new ArrayList<>();

  public boolean isReady() {
    int state = getState();
    if (state != View.STATE_READY) {
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
    left = 0;
    top = 0;
    width = 0;
    height = 0;

    int viewsTop = 0;

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      view.getReady(device, painter, availableWidth, availableHeight, scale);

      if (view.positionedAbsolutely) {
        continue;
      }

      view.left = 0;
      view.top = viewsTop;

      width = Math.max(view.left + view.width, width);
      height = Math.max(view.top + view.height, height);

      viewsTop += view.height;
    }
  }

  @Override
  public void paint(Device device, Painter painter) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).paint(device, painter);
    }
  }
}
