package java.franca.graphics.animations;

import contracted.java.franca.TranspilableClass;
import java.franca.graphics.device.Router;
import java.franca.graphics.views.Page;
import java.franca.graphics.views.View;

public class Tween extends TranspilableClass {

  Page page = null;
  View view = null;
  Ease ease = null;

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

  public boolean needsRepainting(Router router) {
    if (ease == null) {
      // one shot animation
      return true;
    }
    return ease.tick(router, registeredTime);
  }

  public boolean needsNextRepainting(Router router) {
    if (ease == null) {
      // one shot animation
      return false;
    }
    return ease.isRunning();
  }
}
