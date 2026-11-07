package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Painter;

public class PanelView extends BoxView {

  public Float viewsScale = null;

  public PanelViewport panelViewport = null;

  public PanelView() {
    super();
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    if (viewsScale != null) {
      scale *= viewsScale;
    }
    super.getReady(device, painter, availableWidth, availableHeight, scale);
    width = availableWidth;
    if (panelViewport != null) {
      height = availableHeight;
    }

    setState(1);
  }
}

