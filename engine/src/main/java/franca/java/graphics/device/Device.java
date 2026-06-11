package franca.java.graphics.device;

import franca.java.core.contracted.TranspilableClass;
import franca.java.graphics.animations.Tween;
import franca.java.graphics.animations.Tween;

public class Device extends TranspilableClass {

  // animation orchestration

  private int lastTweenId = 0;
  private Tween firstTween = null;

  public long getTime() {
    return 0L;
  }

  public void readFile(String path, StringConsumer callback) {
  }

  public void writeFile(String path, String content, IntegerConsumer callback) {
  }

  public synchronized void requestRepainting() {
    // one-shot animation
    Tween tween = new Tween(null, null, 0L, 0);
    registerTween(tween);
  }

  public synchronized void registerTween(Tween tween) {

    // the new animation becomes the first in the chain because registerTween can be called from Tween.needsRepainting

    lastTweenId++;
    tween.tweenId = lastTweenId;
    tween.registeredTime = getTime();

    tween.nextTween = firstTween;
    if (firstTween != null) {
      firstTween.previousTween = tween;
    }
    firstTween = tween;

    startRepainting();
  }

  public void startRepainting() {
  }

  public synchronized void removeTween(Tween tween) {

    Tween currentTween = firstTween;
    while (currentTween != null) {
      if (currentTween.tweenId == tween.tweenId) {
        Tween previousTween = currentTween.previousTween;
        Tween nextTween = currentTween.nextTween;

        if (previousTween != null) {
          previousTween.nextTween = nextTween;
        }
        if (nextTween != null) {
          nextTween.previousTween = previousTween;
        }
        if (currentTween == firstTween) {
          firstTween = nextTween;
        }
        break;
      }
      currentTween = currentTween.nextTween;
    }
    if (firstTween == null) {
      lastTweenId = 0;
    }
  }

  public synchronized boolean needsRepainting() {

    boolean result = false;

    Tween currentTween = firstTween;
    while (currentTween != null) {
      result = currentTween.needsRepainting(this) || result;
      if (currentTween.getTicker() == null) {
        // it's one-shot tween
        Tween previousTween = currentTween.previousTween;
        Tween nextTween = currentTween.nextTween;
        if (previousTween != null) {
          previousTween.nextTween = nextTween;
        }
        if (nextTween != null) {
          nextTween.previousTween = previousTween;
        }
        if (currentTween.tweenId == firstTween.tweenId) {
          firstTween = nextTween;
        }
        currentTween.destroy();
        currentTween = nextTween;
      } else {
        currentTween = currentTween.nextTween;
      }
    }
    return result;
  }

  public synchronized boolean needsNextRepainting() {

    boolean result = false;

    Tween currentTween = firstTween;
    while (currentTween != null) {
      if (currentTween.getTicker() == null) {
        // wow! one-shot tween has been created during paint(), welcome to next needsRepainting
        result = true;
      } else {
        // always run needsNextRepainting
        result = currentTween.needsNextRepainting(this) || result;
      }
      currentTween = currentTween.nextTween;
    }
    return result;
  }
}

