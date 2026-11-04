package franca.java.ui.animations;

import franca.java.TranspilableClass;
import franca.java.ui.Router;

public class Ease extends TranspilableClass {

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

  public boolean isOver() {
    return currentValue == targetValue;
  }

  public void tick(Router router, long elapsedTime) {
    // make action
  }
}
