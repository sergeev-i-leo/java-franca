package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Painter;

import java.util.ArrayList;

public class PageView extends PanelView {

  public ArrayList<View> views = new ArrayList<>();

  public int left = 0;
  public int top = 0;
  public int width = 0;
  public int height = 0;

  public PageView() {
    super();
    panelViewport = new PanelViewport(this, PanelViewport.SCROLLABLE_HORIZONTALLY + PanelViewport.SCROLLABLE_VERTICALLY);
  }

  @Override
  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    left = 0;
    top = 0;
    width = availableWidth;
    height = availableHeight;
    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      view.getReady(device, painter, availableWidth, availableHeight, 1f);
    }
  }

  @Override
  public void paint(Device device, Painter painter) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).paint(device, painter);
    }
  }

  @Override
  public void didPainted(Device device, Painter painter) {
    for (int i = 0; i < views.size(); i++) {
      callDidPainted(device, painter, views.get(i));
    }
    didPainted(device, painter);
  }

  public void callDidPainted(Device device, Painter painter, View view) {
    if (view instanceof GroupView) {
      GroupView groupView = (GroupView) view;
      for (int i = 0; i < groupView.views.size(); i++) {
        callDidPainted(device, painter, groupView.views.get(i));
      }
    }
    view.didPainted(device, painter);
  }

  @Override
  public void handlePointerDown(Device device, int painterX, int painterY, int pointedX, int pointedY, int buttonNumber) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).handlePointerDown(device, 0, 0, pointedX, pointedY, buttonNumber);
    }
  }
}
