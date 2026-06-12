package franca.java.graphics.test_components;

import franca.java.graphics.animations.Ease;
import franca.java.graphics.animations.Tween;
import franca.java.graphics.device.Painter;
import franca.java.graphics.device.Router;
import franca.java.graphics.views.Page;
import franca.java.graphics.views.View;

import java.util.Random;

public class TestView0 extends View {

  MyTween myTween = null;

  @Override
  public void paint(Router router, Painter painter, Page page) {

    float x = new Random().nextInt(50) + 50f;
    float y = new Random().nextInt(50) + 50f;

    if (myTween != null) {
      painter.paintText(String.valueOf(myTween.getEase().currentValue), x, y, "", 255);
    } else {
      painter.paintText("PAINT", x, y, "", 255);
    }
  }

  @Override
  public void handlePointerDown(Router router, Page page, float painterX, float painterY, float pointedX, float pointedY, int buttonNumber) {
    if (buttonNumber == 1) {
      removeViewAnimation(router);
      myTween = new MyTween(page, this);
      router.registerTween(myTween);
    } else {
      router.requestRepainting();
    }
  }

  public void removeViewAnimation(Router router) {
    if (myTween != null) {
      router.removeTween(myTween);
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
    public boolean needsRepainting(Router router) {
      boolean result = super.needsRepainting(router);
      if (!result) {
        testView0.removeViewAnimation(router);
        return false;
      }

      return true;
    }
  }
}
