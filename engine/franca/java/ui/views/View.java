package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Page;
import franca.java.ui.Painter;

public class View {

  public static int STATE_IDLE = 0;
  public static int STATE_GETTING_READY = 1;
  public static int STATE_READY = 1;

  public int left = 0;
  public int top = 0;
  public int width = 0;
  public int height = 0;

  public boolean positionedAbsolutely = false;

  volatile private int state = View.STATE_IDLE;

  public void setState(int state) {
    this.state = state;
  }

  public int getState() {
    return state;
  }

  public boolean isReady() {
    return state == View.STATE_READY;
  }

  public void getReady(int availableWidth, int availableHeight, Float scale) {
    left = 0;
    top = 0;
    width = 0;
    height = 0;
  }

  public void paint(Device device, Painter painter, Page page) {
  }

  public void didPainted(Device device, Page page) {
  }

  public void handlePointerDown(Device device, Page page, int painterX, int painterY, int pointedX, int pointedY, int buttonNumber) {
  }
}
