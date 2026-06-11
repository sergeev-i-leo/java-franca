package franca.java.graphics.animations;

import franca.java.core.contracted.TranspilableClass;
import franca.java.graphics.device.Device;
import franca.java.graphics.views.Page;
import franca.java.graphics.views.View;

public class Tween extends TranspilableClass {

  Page page;
  View view;
  Ease ease;

  public int tweenId = 0;
  public long registeredTime = 0L;
  public Tween previousTween = null;
  public Tween nextTween = null;

  public Tween(Page page, View view, long duration, int tickerType) {
    this.page = page;
    this.view = view;
    switch (tickerType) {
      case Ease.EASE_LINEAR:
        ease = new LinearEase(0, 100, duration);
        break;
      default:
        // repaint just once
        ease = null;
        break;
    }
  }

  public void setEase(Ease ease) {
    this.ease = ease;
  }

  public Ease getEase() {
    return ease;
  }

  public boolean needsRepainting(Device device) {
    if (ease == null) {
      // one shot animation
      return true;
    }
    return ease.tick(device, registeredTime);
  }

  public boolean needsNextRepainting(Device device) {
    if (ease == null) {
      // one shot animation
      return false;
    }
    return ease.isRunning();
  }
}
