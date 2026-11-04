package franca.java.ui.animations;

import franca.java.ui.Device;

public class LinearEase extends Ease {

  public LinearEase(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void tick(Device device, long elapsedTime) {

    // returns true when needs repainting

    if (isOver()) {
      return;
    }

    if (elapsedTime >= duration) {
      // is over
      currentValue = targetValue;
      return;
    }

    currentValue = initialValue + (int) ((targetValue - initialValue) * elapsedTime / (double) duration);
  }
}

