package franca.java.ui.animations;

import franca.java.ui.Device;
import franca.java.ui.Page;
import franca.java.ui.View;

public class InOutAnimation extends Animation {

  public InOutAnimation(Device device, Page page, View view, int initialValue, int targetValue, long duration) {
    super(device, page, view, initialValue, targetValue, duration);
  }

  @Override
  public void tick() {
    if (isOver()) {
      return;
    }

    if (getElapsedTime() >= duration) {
      currentValue = targetValue;
      return;
    }

    float time = getElapsedTime() / (float) duration;

    if (time < 0.5f) {
      time = 4 * time * time * time;
    } else {
      float f = -2 * time + 2;
      time = 1 - (f * f * f) / 2;
    }

    currentValue = initialValue + (int) ((targetValue - initialValue) * time);
  }
}
