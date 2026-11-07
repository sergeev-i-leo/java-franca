package franca.java.ui.animations;

import franca.java.ui.Device;

public class LinearAnimation extends Animation {

  public LinearAnimation(Device device, int initialValue, int targetValue, long duration) {
    super(device, initialValue, targetValue, duration);
  }

  @Override
  public void tick() {

    // returns true when needs repainting

    if (isOver()) {
      return;
    }

    if (getElapsedTime() >= duration) {
      // is over
      currentValue = targetValue;
      return;
    }

    currentValue = initialValue + (int) ((targetValue - initialValue) * getElapsedTime() / (double) duration);
  }
}

