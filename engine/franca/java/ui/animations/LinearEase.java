package franca.java.ui.animations;

import franca.java.ui.Router;

public class LinearEase extends Ease {

  public LinearEase(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void tick(Router router, long elapsedTime) {

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

