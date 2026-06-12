package franca.java.graphics.animations;

import contracted.franca.java.TranspilableClass;
import franca.java.graphics.device.Router;

public class Ease extends TranspilableClass {

  public static final int EASE_LINEAR = 1;

  public int initialValue;
  public int currentValue;
  public int targetValue;

  public long duration;

  public Ease(int initialValue, int targetValue, long duration) {
    this.initialValue = initialValue;
    this.currentValue = this.initialValue;
    this.targetValue = targetValue;

    this.duration = duration;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  public boolean isRunning() {
    return currentValue != targetValue;
  }

  public boolean tick(Router router, long startedTime) {
    // returns true when needs repainting
    return true;
  }
}

