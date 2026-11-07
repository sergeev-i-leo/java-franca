package franca.java;

import franca.java.ui.Painter;
import franca.java.ui.Device;
import franca.java.ui.animations.LinearAnimation;
import franca.java.ui.views.View;

import java.util.Random;

public class TestView0 extends View {

  MyAnimation myAnimation = null;

  @Override
  public void paint(Device device, Painter painter) {

    float x = (new Random().nextInt(50) + 50f) * Painter.PRECISION;
    float y = (new Random().nextInt(50) + 50f) * Painter.PRECISION;

    if (myAnimation != null) {
      painter.paintText(String.valueOf(myAnimation.currentValue), (int) x, (int) y, "", 255, 1f);
    } else {
      painter.paintText("PAINT", (int) x, (int) y, "", 255, 1f);
    }
  }

  public void startSpinning(Device device) {
    device.abortAnimation(myAnimation);
    myAnimation = new MyAnimation(device, this);
    device.registerAnimation(myAnimation);
  }
}

class MyAnimation extends LinearAnimation {

  TestView0 testView0;

  MyAnimation(Device device, TestView0 testView0) {
    super(device, 0, 100, 500L);

    this.testView0 = testView0;
  }

  @Override
  public void onDone() {
    testView0.myAnimation = null;
  }
}

