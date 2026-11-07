package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Painter;

import java.util.ArrayList;

public class PageView extends PanelView {

  public Device device;

  public ArrayList<View> views = new ArrayList<>();

  public int left = 0;
  public int top = 0;
  public int width = 0;
  public int height = 0;

  public PageView(Device device) {
    super();
    this.device = device;
    panelViewport = new PanelViewport(this, PanelViewport.SCROLLABLE_HORIZONTALLY + PanelViewport.SCROLLABLE_VERTICALLY);
  }

  public void getReady(Device device, Painter painter, int left, int top, int width, int height) {
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;
    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      view.getReady(device, painter, width, height, 1f);

    }
  }

  public void paint(Painter painter) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).paint(device, painter);
    }
  }

  public void didPainted() {
    for (int i = 0; i < views.size(); i++) {
      callDidPainted(views.get(i));
    }
    didPainted(device);
  }

  public void callDidPainted(View view) {
    if (view instanceof GroupView) {
      GroupView groupView = (GroupView) view;
      for (int i = 0; i < groupView.views.size(); i++) {
        callDidPainted(groupView.views.get(i));
      }
    }
    view.didPainted(device);
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).handlePointerDown(device, 0, 0, pointedX, pointedY, buttonNumber);
    }
  }
}
