package franca.java;

import franca.java.ui.Painter;
import franca.java.ui.Device;
import franca.java.ui.animations.Animation;
import franca.java.ui.views.View;

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

  public int size;
  public long startTime;
  public SpinnerAnimation spinnerAnimation;

  public boolean spinningAllowed = true;

  public SpinnerView(int size) {
    super();
    this.size = size;
  }

  public void paint(Device device, Painter painter) {
    if ((spinnerAnimation == null) && (spinningAllowed)) {
      startTime = device.getTime();
      spinnerAnimation = new SpinnerAnimation(device);
      device.registerAnimation(spinnerAnimation);
    }

    if (this.spinnerAnimation == null) {
      return;
    }

    long elapsed = device.getTime() - startTime;
    float outerAngle = (elapsed % OUTER_PERIOD) / (float) OUTER_PERIOD * 360.0f;

    float size = this.size;

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

      painter.fillCircle((int) x, (int) y, (int) radius, COLOR);
    }
  }

  public void startSpinning(Device device) {
    if (spinnerAnimation == null) {
      spinningAllowed = true;
      device.requestPainting();
    }
  }

  public void stopSpinning(Device device) {
    spinningAllowed = false;
    device.abortAnimation(spinnerAnimation);
    spinnerAnimation = null;
  }
}

class SpinnerAnimation extends Animation {

  SpinnerAnimation(Device device) {
    super(device, 0, 0, 0L);
  }

  @Override
  public boolean needsNextPainting() {
    return true;
  }

  @Override
  public void tick() {
  }
}
