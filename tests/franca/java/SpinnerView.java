package franca.java;

import franca.java.ui.Page;
import franca.java.ui.Painter;
import franca.java.ui.Device;
import franca.java.ui.View;
import franca.java.ui.animations.Ease;
import franca.java.ui.animations.Tween;

public class SpinnerView extends View {

  public static final int COLOR = 0xFFFD7E14;

  public static final long[] DOT_DELAYS = {
    -1100,
    -1000,
    -900,
    -800,
    -700,
    -600
  };

  public static final long INNER_PERIOD = 2000L;
  public static final long OUTER_PERIOD = 2500L;

  public float size;
  public long startTime;
  public SpinnerTween spinnerTween;

  public boolean spinningAllowed = true;

  public SpinnerView(float size) {
    super();
    this.size = size;
  }

  public void paint(Device device, Painter painter, Page page) {
    if (this.spinnerTween == null) {
      return;
    }

    long elapsed = device.getTime() - startTime;
    float outerAngle = (elapsed % OUTER_PERIOD) / (float) OUTER_PERIOD * 360.0f;

    float size = this.size / Painter.PRECISION;

    for (int i = 0; i < 6; i++) {
      long dotTime = elapsed + DOT_DELAYS[i];
      long cycleTime = dotTime % INNER_PERIOD;
      if (cycleTime < 0) {
        cycleTime += INNER_PERIOD;
      }

      float time = cycleTime / (float) INNER_PERIOD;
      if (time < 0.5f) {
        time = 4 * time * time * time;
      } else {
        float f = -2 * time + 2;
        time = 1 - (f * f * f) / 2;
      }

      float dotAngle = time * 360.0f;
      float totalAngle = outerAngle + dotAngle;

      float scale = 0.4f + 0.6f * (i / 5.0f);
      float radius = size * 0.2f * scale;

      float x = size / 2 + (float) Math.cos(Math.toRadians(totalAngle)) * size / 2;
      float y = size / 2 + (float) Math.sin(Math.toRadians(totalAngle)) * size / 2;

      painter.fillCircle(x * Painter.PRECISION, y * Painter.PRECISION, radius * Painter.PRECISION, COLOR, 1f);
    }
  }

  @Override
  public void didPainted(Device device, Page page) {
    if ((spinnerTween == null) && (spinningAllowed)) {
      startTime = device.getTime();
      spinnerTween = new SpinnerTween(device);
      device.registerTween(spinnerTween);
    }
  }

  public void startSpinning(Device device) {
    if (spinnerTween == null) {
      spinningAllowed = true;
      device.requestPainting();
    }
  }

  public void stopSpinning(Device device) {
    spinningAllowed = false;
    device.unregisterTween(spinnerTween);
    spinnerTween = null;
  }
}

class SpinnerTween extends Tween {

  SpinnerTween(Device device) {
    super(device, null, null, new Ease(0, 0, 0L));
  }

  @Override
  public boolean needsNextPainting() {
    return true;
  }

  @Override
  public void tick() {
  }
}
