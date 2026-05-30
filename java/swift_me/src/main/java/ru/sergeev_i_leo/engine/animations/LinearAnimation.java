package ru.sergeev_i_leo.engine.animations;

import ru.sergeev_i_leo.engine.Device;
import ru.sergeev_i_leo.engine.Page;

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

