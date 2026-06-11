package franca.java.graphics.animations;

import franca.java.core.contracted.TranspilableClass;
import franca.java.graphics.device.Device;

public class Ticker extends TranspilableClass {

  public int initialValue;
  public int currentValue;
  public int targetValue;

  public long duration;

  public Ticker(int initialValue, int targetValue, long duration) {
    this.initialValue = initialValue;
    this.currentValue = this.initialValue;
    this.targetValue = targetValue;

    this.duration = duration;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  public boolean isTicking() {
    return currentValue != targetValue;
  }

  public boolean tick(Device device, long registeredTime) {
    // returns true when needs repainting
    return true;
  }
}

