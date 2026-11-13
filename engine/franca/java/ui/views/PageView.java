package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Painter;

public class PageView extends PanelView {

  public int left = 0;
  public int top = 0;
  public int width = 0;
  public int height = 0;

  public PageView() {
    super();
    panelViewport = new PanelViewport(this, PanelViewport.SCROLLABLE_HORIZONTALLY + PanelViewport.SCROLLABLE_VERTICALLY);
  }

  @Override
  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
    for (int i = 0; i < innerViews.size(); i++) {
      innerViews.get(i).paint(device, painter, painterX, painterY, scale);
    }
  }

  @Override
  public void handlePointerDown(Device device, int painterX, int painterY, int pointedX, int pointedY, int buttonNumber) {
    for (int i = 0; i < innerViews.size(); i++) {
      innerViews.get(i).handlePointerDown(device, 0, 0, pointedX, pointedY, buttonNumber);
    }
  }
}
