package franca.java.graphics.animations;

import franca.java.expected.TranspilableClass;
import franca.java.expected.Router;

public class Ease extends TranspilableClass {

  public static final int EASE_LINEAR = 1;

  public int initialValue;
  public int currentValue;
  public int targetValue;

  public long duration;

  public Ease(int initialValue, int targetValue, long duration) {
    super();

    this.initialValue = initialValue;
    this.currentValue = this.initialValue;
    this.targetValue = targetValue;

    this.duration = duration;
  }

  public boolean isRunning() {
    return currentValue != targetValue;
  }

  public boolean tick(Router router, long startedTime) {
    // returns true when needs repainting
    return true;
  }
}

