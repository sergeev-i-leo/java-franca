package franca.java.ui;

import java.util.ArrayList;

public class View {

  public int left = 0;
  public int top = 0;
  public int width = 0;
  public int height = 0;

  public ArrayList<View> views = new ArrayList<>();

  public boolean positionedAbsolutely = false;

  public void calculateGeometry(int availableWidth, int availableHeight, Float contentScale) {
    left = 0;
    top = 0;
    width = availableWidth;
    height = availableHeight;

    int viewsTop = 0;

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      view.calculateGeometry(availableWidth, availableHeight, contentScale);

      if (view.positionedAbsolutely) {
        continue;
      }

      view.left = 0;
      view.top = viewsTop;

      width = Math.max(view.left + view.width, width);
      height = Math.max(view.top + view.height, height);

      viewsTop += view.height;
    }
  }

  public void paint(Device device, Painter painter, Page page) {
  }

  public void didPainted(Device device, Page page) {
  }

  public void handlePointerDown(Device device, Page page, int painterX, int painterY, int pointedX, int pointedY, int buttonNumber) {
  }
}
