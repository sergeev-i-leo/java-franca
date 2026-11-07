package franca.java.ui.animations;

import franca.java.ui.Device;

public class Animation {

  public Device device = null;

  public int initialValue;
  public int currentValue;
  public int targetValue;

  public long duration;

  public long registeredTime = 0L;
  public Animation previousAnimation = null;

  public Animation(Device device, int initialValue, int targetValue, long duration) {
    super();

    this.device = device;

    this.initialValue = initialValue;
    this.currentValue = this.initialValue;
    this.targetValue = targetValue;

    this.duration = duration;
  }

  public void tick() {
  }

  public long getElapsedTime() {
    return device.getTime() - registeredTime;
  }

  public boolean needsNextPainting() {
    if (duration == 0L) {
      // one shot animation
      return false;
    }
    return !isOver();
  }

  public boolean isOver() {
    if (duration == 0L) {
      return true;
    }
    return currentValue == targetValue;
  }

  public void onDone() {
  }
}
