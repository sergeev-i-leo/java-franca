package franca.java.ui.views.panels;

import franca.java.ui.Device;
import franca.java.ui.Painter;

public class ZoomablePanelView extends PanelView {

  public int innerViewsZoomPercentage = 100;

  public ZoomablePanelView() {
    super(PanelView.SCROLLABLE_HORIZONTALLY + PanelView.SCROLLABLE_VERTICALLY);
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, int zoomPercentage) {

    if (innerViewsZoomPercentage != 100) {
      zoomPercentage *= innerViewsZoomPercentage / 100;
    }

    super.getReady(device, painter, availableWidth, availableHeight, zoomPercentage);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, int zoomPercentage) {
    if (innerViewsZoomPercentage != 100) {
      zoomPercentage *= innerViewsZoomPercentage / 100;
    }

    super.paint(device, painter, painterX, painterY, zoomPercentage);
  }
}
