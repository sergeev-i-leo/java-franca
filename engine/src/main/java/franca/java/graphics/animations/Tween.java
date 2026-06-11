package franca.java.graphics.animations;

import franca.java.core.contracted.TranspilableClass;
import franca.java.graphics.device.Device;
import franca.java.graphics.views.Page;
import franca.java.graphics.views.View;

public class Tween extends TranspilableClass {

  public static final int TICKER_TYPE_LINEAR = 1;

  Page page;
  View view;
  Ticker ticker;

  public int tweenId = 0;
  public long registeredTime = 0L;
  public Tween previousTween = null;
  public Tween nextTween = null;

  public Tween(Page page, View view, long duration, int tickerType) {
    this.page = page;
    this.view = view;
    switch (tickerType) {
      case Tween.TICKER_TYPE_LINEAR:
        ticker = new LinearTicker(0, 100, duration);
        break;
      default:
        ticker = null;
        break;
    }
  }

  public void setTicker(Ticker ticker) {
    this.ticker = ticker;
  }

  public Ticker getTicker() {
    return ticker;
  }

  public boolean needsRepainting(Device device) {
    if (ticker == null) {
      // one shot animation
      return true;
    }
    return ticker.tick(device, registeredTime);
  }

  public boolean needsNextRepainting(Device device) {
    if (ticker == null) {
      // one shot animation
      return false;
    }
    return ticker.isTicking();
  }
}
