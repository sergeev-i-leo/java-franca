package franca.java.graphics.test_components;

import java.util.Random;

import franca.java.graphics.animations.Ease;
import franca.java.graphics.animations.Tween;
import franca.java.graphics.device.Device;
import franca.java.graphics.views.Page;
import franca.java.graphics.views.View;
import franca.java.graphics.painter.Painter;

public class TestView0 extends View {

  MyTween myTween = null;

  @Override
  public void paint(Device device, Painter painter, Page page) {

    float x = new Random().nextInt(50) + 50f;
    float y = new Random().nextInt(50) + 50f;

    if (myTween != null) {
      painter.paintText(String.valueOf(myTween.getEase().currentValue), x, y, "", 255);
    } else {
      painter.paintText("PAINT", x, y, "", 255);
    }
  }

  @Override
  public void handlePointerDown(Device device, Page page, float painterX, float painterY, float pointedX, float pointedY, int buttonNumber) {
    if (buttonNumber == 1) {
      removeViewAnimation(device);
      myTween = new MyTween(page, this);
      device.registerTween(myTween);
    } else {
      device.requestRepainting();
    }
  }

  public void removeViewAnimation(Device device) {
    if (myTween != null) {
      device.removeTween(myTween);
      myTween = null;
    }
  }

  static class MyTween extends Tween {

    TestView0 testView0;

    MyTween(Page page, TestView0 testView0) {
      super(page, testView0, 500, Ease.EASE_LINEAR);

      this.testView0 = testView0;
    }

    @Override
    public boolean needsRepainting(Device device) {
      boolean result = super.needsRepainting(device);
      if (!result) {
        testView0.removeViewAnimation(device);
        return false;
      }

      return true;
    }
  }
}
