package franca.java.ui.views.panels;

import franca.java.ui.Device;
import franca.java.ui.Painter;

public class ScalablePanelView extends PanelView {

  public Float innerViewsScale = null;

  public ScalablePanelView(int scrollable) {
    super(scrollable);
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, float scale) {

    if (innerViewsScale != null) {
      scale *= innerViewsScale;
    }

    super.getReady(device, painter, availableWidth, availableHeight, scale);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, float scale) {
    if (innerViewsScale != null) {
      scale *= innerViewsScale;
    }
    super.paint(device, painter, painterX, painterY, scale);
  }
}
