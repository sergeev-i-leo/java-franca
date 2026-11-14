package franca.java.ui.views.panel;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.views.GroupView;

public class PanelView extends GroupView {

  public PanelViewport panelViewport = null;

  public PanelView() {
    super();
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    super.getReady(device, painter, availableWidth, availableHeight, scale);
    width = availableWidth;
    if (panelViewport != null) {
      height = availableHeight;
    }
  }
}

