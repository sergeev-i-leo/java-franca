package franca.java.graphics.animations;

import franca.java.graphics.device.Device;

public class LinearTicker extends Ticker {

  public LinearTicker(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public boolean tick(Device device, long registeredTime) {

    // returns true when needs repainting

    long currentTime = device.getTime();

    if (currentTime >= registeredTime + duration) {
      currentValue = targetValue;
      return true;
    }

    currentValue = (int) (initialValue + (targetValue - initialValue) * (currentTime - registeredTime) / duration);
    return true;
  }
}

