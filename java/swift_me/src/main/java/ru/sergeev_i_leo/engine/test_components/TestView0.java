package ru.sergeev_i_leo.engine.test_components;

import java.util.Random;

import ru.sergeev_i_leo.engine.Device;
import ru.sergeev_i_leo.engine.Page;
import ru.sergeev_i_leo.engine.painter.Painter;
import ru.sergeev_i_leo.engine.View;
import ru.sergeev_i_leo.engine.animations.LinearAnimation;

public class TestView0 extends View {

  ViewAnimation viewAnimation = null;

  @Override
  public void paint(Device device, Painter painter, Page page) {

    float x = new Random().nextFloat(100f) + 50f;
    float y = new Random().nextFloat(100f) + 50f;

    if (viewAnimation != null) {
      painter.paintText(String.valueOf(viewAnimation.currentValue), x, y, "", 255);
    } else {
      painter.paintText("PAINT", x, y, "", 255);
    }
  }

  @Override
  public void handlePointerDown(Device device, Page page, float painterX, float painterY, float pointedX, float pointedY, int buttonNumber) {
    if (buttonNumber == 1) {
      removeViewAnimation(page);
      viewAnimation = new ViewAnimation(0, 100, 500, this);
      page.registerAnimation(viewAnimation);
    } else {
      page.requestRepainting();
    }
  }

  public void removeViewAnimation(Page page) {
    if (viewAnimation != null) {
      page.removeAnimation(viewAnimation);
      viewAnimation = null;
    }
  }
}

class ViewAnimation extends LinearAnimation {

  TestView0 testView0;

  ViewAnimation(float initialValue, float targetValue, long duration, TestView0 testView0) {
    super(initialValue, targetValue, duration);

    this.testView0 = testView0;
  }

  @Override
  public boolean needsRepainting(Device device, Page page, long time) {
    boolean result = super.needsRepainting(device, page, time);
    if (!result) {
      testView0.removeViewAnimation(page);
      return false;
    }

    return true;
  }
}
