package franca.java.graphics.animations;

import franca.java.graphics.device.Device;

public class LinearEase extends Ease {

  public LinearEase(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public boolean tick(Device device, long startedTime) {

    // returns true when needs repainting

    if (currentValue == targetValue) {
      return false;
    }

    long currentTime = device.getTime();

    if (currentTime >= startedTime + duration) {
      currentValue = targetValue;
      return true;
    }

    currentValue = (int) (initialValue + (targetValue - initialValue) * (currentTime - startedTime) / duration);
    return true;
  }
}

