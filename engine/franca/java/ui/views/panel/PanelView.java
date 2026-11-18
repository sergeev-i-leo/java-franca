package franca.java.ui.views.panel;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.GroupView;
import franca.java.ui.views.View;

public class PanelView extends GroupView {

  public PanelViewport panelViewport = null;

  public PanelView() {
    super();
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, float scale) {
    super.getReady(device, painter, availableWidth, availableHeight, scale);
    width = availableWidth;
    if (panelViewport != null) {
      height = availableHeight;
    }
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, float scale) {
    if (innerViewsScale != null) {
      scale *= innerViewsScale;
    }
    int innerViewsX = paddingLeft;
    int innerViewsY = paddingTop;
    if (panelViewport != null) {
      innerViewsX += panelViewport.innerViewsX;
      innerViewsX += panelViewport.paddingLeft;
      innerViewsY += panelViewport.innerViewsY;
      innerViewsY += panelViewport.paddingTop;
    }
    for (int i0 = 0; i0 <= maxInnerLayer; i0++) {
      for (int i1 = 0; i1 < innerViews.size(); i1++) {
        View view = innerViews.get(i1);
        if (view.layer == i0) {
          view.paint(device, painter, painterX + left + innerViewsX, painterY + top + innerViewsY, scale);
        }
      }
    }
  }
}

