package franca.java.graphics.animations;

import franca.java.graphics.renderer.Page;
import franca.java.graphics.contract.Device;

public class LinearAnimation extends Animation {

  public LinearAnimation(float initialValue, float targetValue, long duration) {
    super(initialValue, targetValue, duration);
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  @Override
  public boolean needsRepainting(Device device, Page page, long time) {

    if (duration == 0L) {
      return false;
    }

    if (time >= registeredTime + duration) {
      currentValue = targetValue;
      duration = 0L;
      return true;
    }

    currentValue = initialValue + (targetValue - initialValue) * (time - registeredTime) / duration;

    return true;
  }
}

