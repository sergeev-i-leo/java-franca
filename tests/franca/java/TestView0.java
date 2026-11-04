package franca.java;

import franca.java.ui.Page;
import franca.java.ui.Painter;
import franca.java.ui.Device;
import franca.java.ui.View;
import franca.java.ui.animations.LinearEase;
import franca.java.ui.animations.Tween;

import java.util.Random;

public class TestView0 extends View {

  MyTween myTween = null;

  @Override
  public void paint(Device device, Painter painter, Page page) {

    float x = (new Random().nextInt(50) + 50f) * Painter.PRECISION;
    float y = (new Random().nextInt(50) + 50f) * Painter.PRECISION;

    if (myTween != null) {
      painter.paintText(String.valueOf(myTween.ease.currentValue), x, y, "", 255, 1f);
    } else {
      painter.paintText("PAINT", x, y, "", 255, 1f);
    }
  }

  public void startSpinning(Device device, Page page) {
    device.unregisterTween(myTween);
    myTween = new MyTween(device, page, this);
    device.registerTween(myTween);
  }
}

class MyTween extends Tween {

  TestView0 testView0;

  MyTween(Device device, Page page, TestView0 testView0) {
    super(device, page, testView0, new LinearEase(0, 100, 500L));

    this.testView0 = testView0;
  }

  @Override
  public void onDone() {
    testView0.myTween = null;
  }
}

