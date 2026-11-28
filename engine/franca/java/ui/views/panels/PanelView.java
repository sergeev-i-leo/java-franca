package franca.java.ui.views.panels;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.GroupView;
import franca.java.ui.views.View;

public class PanelView extends GroupView {

  public static int SCROLLABLE_HORIZONTALLY = 1;
  public static int SCROLLABLE_VERTICALLY = 2;

  public int scrollable;

  public int viewportX = 0;
  public int viewportY = 0;

  public PanelView(int scrollable) {
    super();
    this.scrollable = scrollable;
  }

  @Override
  public void prepare(Device device, Painter painter, int availableWidth, int availableHeight, int zoomPercentage) {

    super.prepare(device, painter, availableWidth, availableHeight, zoomPercentage);

    if (scrollable == 0) {
      width = availableWidth;
    }
    if ((scrollable & PanelView.SCROLLABLE_HORIZONTALLY) > 0) {
      width = availableWidth;
    }
    if ((scrollable & PanelView.SCROLLABLE_VERTICALLY) > 0) {
      height = availableHeight;
    }
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, int zoomPercentage) {

    for (int i0 = 0; i0 <= maxInnerLayer; i0++) {
      for (int i1 = 0; i1 < innerViews.size(); i1++) {
        View view = innerViews.get(i1);
        if (view.layer == i0) {
          view.paint(device, painter, painterX + left + paddingLeft + viewportX, painterY + top + paddingTop + viewportY, zoomPercentage);
        }
      }
    }
  }
}

