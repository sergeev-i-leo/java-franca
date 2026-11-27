package franca.java.ui.views.panels;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.View;

public class ZoomablePanelView extends PanelView {

  public int innerViewsZoomPercentage = 100;

  public ZoomablePanelView() {
    super(PanelView.SCROLLABLE_HORIZONTALLY + PanelView.SCROLLABLE_VERTICALLY);
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, int zoomPercentage) {
    if (isReady()) {
      return;
    }
    if (getReadiness() == 0) {
      return;
    }
    setReadiness(0);

    width = availableWidth;
    height = availableHeight;

    if (innerViewsZoomPercentage != 100) {
      zoomPercentage = zoomPercentage * innerViewsZoomPercentage / 100;
      availableWidth = availableWidth * innerViewsZoomPercentage / 100;
      availableHeight = availableHeight * innerViewsZoomPercentage / 100;
    }

    maxInnerLayer = 0;

    int viewTop = paddingTop;

    for (int i = 0; i < innerViews.size(); i++) {
      View view = innerViews.get(i);

      maxInnerLayer = Math.max(view.layer, maxInnerLayer);

      if (view.layer == 0) {
        view.left = paddingLeft;
        view.top = viewTop;
      }

      view.getReady(device, painter, availableWidth, availableHeight, zoomPercentage);

      if (view.layer > 0) {
        continue;
      }

      viewTop += view.height;
    }

    setReadiness(1);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, int zoomPercentage) {
    if (innerViewsZoomPercentage != 100) {
      zoomPercentage = zoomPercentage * innerViewsZoomPercentage / 100;
    }

    super.paint(device, painter, painterX, painterY, zoomPercentage);
  }
}
