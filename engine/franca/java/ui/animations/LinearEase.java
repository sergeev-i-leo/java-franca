package franca.java.ui.animations;

import franca.java.ui.Router;

public class LinearEase extends Ease {

  public LinearEase(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void tick(Router router, long startedTime) {

    // returns true when needs repainting

    if (currentValue == targetValue) {
      // was over
      return;
    }

    long currentTime = router.getTime();

    if (currentTime >= startedTime + duration) {
      // is over
      currentValue = targetValue;
      return;
    }

    currentValue = initialValue + (int) ((targetValue - initialValue) * (currentTime - startedTime) / (double) duration);
  }
}

