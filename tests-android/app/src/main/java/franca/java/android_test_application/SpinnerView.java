package franca.java.android_test_application;

import franca.java.ui.Page;
import franca.java.ui.Painter;
import franca.java.ui.Router;
import franca.java.ui.View;
import franca.java.ui.animations.Ease;
import franca.java.ui.animations.Tween;

public class SpinnerView extends View {

  public float size;
  public long startTime;
  public SpinnerTween spinnerTween;

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

  public SpinnerView(float size) {
    super();
    this.size = size;
  }

  public void paint(Router router, Painter painter, Page page) {
    if (this.spinnerTween == null) {
      return;
    }

    long elapsed = router.getTime() - startTime;

    float outerAngle = (elapsed % OUTER_PERIOD) / (float) OUTER_PERIOD * 360.0f;

    for (int i = 0; i < 6; i++) {
      long dotTime = elapsed + DOT_DELAYS[i];
      if (dotTime < 0) dotTime += INNER_PERIOD;

      float time = (dotTime % INNER_PERIOD) / (float) INNER_PERIOD;
      if (time < 0.5f) {
        time = 4 * time * time * time;
      } else {
        float f = -2 * time + 2;
        time = 1 - (f * f * f) / 2;
      }

      float dotAngle = time * 360.0f;
      float scale = 0.4f + 0.6f * time;

      float totalAngle = outerAngle + dotAngle;
      float x = size / 2 + (float) Math.cos(Math.toRadians(totalAngle)) * size / 2;
      float y = size / 2 + (float) Math.sin(Math.toRadians(totalAngle)) * size / 2;
      float radius = size * 0.25f * scale;

      painter.fillCircle(x * Painter.PRECISION, y * Painter.PRECISION, radius * Painter.PRECISION, COLOR, 1f);
    }
  }

  @Override
  public void didPainted(Router router, Page page) {
    if (spinnerTween == null) {
      startTime = router.getTime();
      spinnerTween = new SpinnerTween(router);
      router.registerTween(spinnerTween);
    }
  }
}

class SpinnerTween extends Tween {

  SpinnerTween(Router router) {
    super(router, null, null, new Ease(0, 0, 0L));
  }

  @Override
  public boolean needsNextPainting() {
    return true;
  }

  @Override
  public void tick() {
  }
}
