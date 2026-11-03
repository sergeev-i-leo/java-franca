package franca.java;

import franca.java.ui.Page;
import franca.java.ui.Painter;
import franca.java.ui.Router;
import franca.java.ui.View;
import franca.java.ui.animations.LinearEase;
import franca.java.ui.animations.Tween;

import java.util.Random;

public class TestView0 extends View {

  MyTween myTween = null;

  @Override
  public void paint(Router router, Painter painter, Page page) {

    int x = (new Random().nextInt(50) + 50) * Painter.MULTIPLIER;
    int y = (new Random().nextInt(50) + 50) * Painter.MULTIPLIER;

    if (myTween != null) {
      painter.paintText(String.valueOf(myTween.ease.currentValue), x, y, "", 255, 1f);
    } else {
      painter.paintText("PAINT", x, y, "", 255, 1f);
    }
  }

  @Override
  public void handlePointerDown(Router router, Page page, int painterX, int painterY, int pointedX, int pointedY, int buttonNumber) {
    if (buttonNumber == 1) {
      router.unregisterTween(myTween);
      myTween = new MyTween(router, page, this);
      router.registerTween(myTween);
    } else {
      router.requestPainting();
    }
  }
}

class MyTween extends Tween {

  TestView0 testView0;

  MyTween(Router router, Page page, TestView0 testView0) {
    super(router, page, testView0, new LinearEase(0, 100, 500L));

    this.testView0 = testView0;
  }

  @Override
  public void onDone() {
    testView0.myTween = null;
  }
}

