package franca.java.ui.views.panels;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.GroupView;
import franca.java.ui.views.View;

public class PanelView extends GroupView {

  public static int SCROLLABLE_HORIZONTALLY = 1;
  public static int SCROLLABLE_VERTICALLY = 2;

  public int paddingLeft = 0;
  public int paddingTop = 0;
  public int paddingRight = 0;
  public int paddingBottom = 0;

  public int scrollable;

  public int xOffset = 0;
  public int yOffset = 0;

  public PanelView(int scrollable) {
    super();
    this.scrollable = scrollable;
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, float scale) {

    super.getReady(device, painter, availableWidth, availableHeight, scale);

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
  public void paint(Device device, Painter painter, int painterX, int painterY, float scale) {

    for (int i0 = 0; i0 <= maxInnerLayer; i0++) {
      for (int i1 = 0; i1 < innerViews.size(); i1++) {
        View view = innerViews.get(i1);
        if (view.layer == i0) {
          view.paint(device, painter, painterX + left + paddingLeft + xOffset, painterY + top + paddingTop + yOffset, scale);
        }
      }
    }
  }
}

