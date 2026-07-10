package franca.java.graphics.animations;

import franca.java.graphics.device.Router;

public class LinearEase extends Ease {

  public LinearEase(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public boolean tick(Router router, long startedTime) {

    // returns true when needs repainting

    if (currentValue == targetValue) {
      // was over
      return false;
    }

    long currentTime = router.getTime();

    if (currentTime >= startedTime + duration) {
      // is over
      currentValue = targetValue;
      return true;
    }

    currentValue = initialValue + (int) ((targetValue - initialValue) * (currentTime - startedTime) / (double) duration);

    return true;
  }
}

