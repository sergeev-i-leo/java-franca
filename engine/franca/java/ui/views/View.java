package franca.java.ui.views;

import franca.java.ui.Device;
import franca.java.ui.Painter;
import franca.java.ui.TextStyle;

public class View {

  public int left = 0;
  public int top = 0;
  public int width = 0;
  public int height = 0;

  public int zIndex = 0;

  public TextStyle textStyle = null;

  volatile private int readiness = -1;

  public void setReadiness(int readiness) {
    this.readiness = readiness;
  }

  public int getReadiness() {
    return readiness;
  }

  public void getReady(Device device, Painter painter, int availableWidth, int availableHeight, Float scale) {
    if (isReady()) {
      return;
    }
    if (getReadiness() == 0) {
      return;
    }
    setReadiness(0);

    width = 0;
    height = 0;

    setReadiness(1);
  }

  public boolean isReady() {
    return readiness == 1;
  }

  public void paint(Device device, Painter painter, int painterX, int painterY, Float scale) {
  }

  public void handlePointerDown(Device device, int painterX, int painterY, int pointedX, int pointedY, int buttonNumber) {
  }
}
