package franca.java.ui;

import franca.java.TranspilableClass;
import franca.java.ui.animations.Ease;
import franca.java.ui.animations.Tween;

/**
 * Router manages all animations.
 * Ownership rule: whoever creates the animation (via registerAnimation) does NOT own it, Router owns it.
 * Page calls destroy() when removing animation from the chain.
 * External code must NEVER call destroy() on animations directly.
 */

public class Router extends TranspilableClass {

  public Page topPage = null;

  // animation orchestration

  private int lastTweenId = 0;
  private Tween firstTween = null;

  public long getTime() {
    return 0L;
  }

  public void pushPage(Page page) {
    page.nextPage = topPage;
    topPage = page;
  }

  public void popPage() {
    if (topPage != null) {
      topPage = topPage.nextPage;
    }
  }

  public void requestPainting() {
    // one-shot animation
    Tween tween = new Tween(this, null, null, new Ease(0, 0, 0L));
    registerTween(tween);
  }

  public void registerTween(Tween tween) {

    // the new animation becomes the first in the chain because registerTween can be called from Tween.needsRepainting

    lastTweenId++;
    tween.tweenId = lastTweenId;
    tween.registeredTime = getTime();

    tween.nextTween = firstTween;
    if (firstTween != null) {
      firstTween.previousTween = tween;
    }
    firstTween = tween;

    startPainting();
  }

  public void startPainting() {
  }

  public boolean needsPainting() {

    Tween currentTween = firstTween;
    if (currentTween == null) {
      lastTweenId = 0;
      return false;
    }

    while (currentTween != null) {
      currentTween.tick();
      currentTween = currentTween.nextTween;
    }

    return true;
  }

  public void paint(Painter painter) {
    if (topPage != null) {
      topPage.paint(painter);
    }
  }

  public boolean needsNextPainting() {

    Tween currentTween = firstTween;
    if (currentTween == null) {
      lastTweenId = 0;
      return false;
    }

    while (currentTween != null) {
      if (currentTween.needsNextPainting()) {
        currentTween = currentTween.nextTween;
        continue;
      }

      currentTween.onDone();
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
      currentTween = nextTween;
    }

    if (firstTween == null) {
      lastTweenId = 0;
    }

    return true;
  }

  public void unregisterTween(Tween tween) {

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

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (topPage != null) {
      topPage.handlePointerDown(pointedX, pointedY, buttonNumber);
    }
  }
}
