package franca.java.ui.animations;

import franca.java.ui.Router;

public class InOutEase extends Ease {

  public InOutEase(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void tick(Router router, long elapsedTime) {
    if (isOver()) {
      return;
    }

    if (elapsedTime >= duration) {
      currentValue = targetValue;
      return;
    }

    float time = elapsedTime / (float) duration;

    if (time < 0.5f) {
      time = 4 * time * time * time;
    } else {
      float f = -2 * time + 2;
      time = 1 - (f * f * f) / 2;
    }

    currentValue = initialValue + (int) ((targetValue - initialValue) * time);
  }
}
