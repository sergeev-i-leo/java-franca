package franca.java.ui.views;

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

  public void getReady(int availableWidth, int availableHeight, Float scale) {
    left = 0;
    top = 0;
    width = 0;
    height = 0;

    int viewsTop = 0;

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      view.getReady(availableWidth, availableHeight, scale);

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

}
