package franca.java.graphics.animations;

import franca.java.graphics.device.Router;

public class LinearEase extends Ease {

  public LinearEase(int initialValue, int targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public boolean tick(Router router, long startedTime) {

    // returns true when needs repainting

    if (currentValue == targetValue) {
      return false;
    }

    long currentTime = router.getDevice().getTime();

    if (currentTime >= startedTime + duration) {
      currentValue = targetValue;
      return true;
    }

    currentValue = (int) (initialValue + (targetValue - initialValue) * (currentTime - startedTime) / duration);
    return true;
  }
}

