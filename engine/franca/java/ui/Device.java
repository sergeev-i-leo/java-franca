package franca.java.ui;

import franca.java.TranspilableClass;
import franca.java.ui.animations.Ease;
import franca.java.ui.animations.Tween;

/**
 * Device manages all animations.
 * Ownership rule: whoever creates the animation (via registerAnimation) does NOT own it, Device owns it.
 * Page calls destroy() when removing animation from the chain.
 * External code must NEVER call destroy() on animations directly.
 */

public class Device extends TranspilableClass {

  public RouterPage routerPage = null;

  // animation orchestration

  public Tween lastTween = null;

  public long getTime() {
    return 0L;
  }

  public void requestPainting() {
    // one-shot animation
    Tween tween = new Tween(this, null, null, new Ease(0, 0, 0L));
    registerTween(tween);
  }

  public void registerTween(Tween tween) {
    tween.previousTween = lastTween;
    lastTween = tween;

    tween.registeredTime = getTime();

    startPainting();
  }

  public void startPainting() {
    // real painting
  }

  public boolean needsPainting() {

    Tween currentTween = lastTween;
    if (currentTween == null) {
      return false;
    }

    while (currentTween != null) {
      currentTween.tick();
      currentTween = currentTween.previousTween;
    }

    return true;
  }

  public void paint(Painter painter) {
    if (routerPage != null) {
      routerPage.paint(painter);
    }
  }

  public boolean needsNextPainting() {

    //  mark for deletion

    Tween currentTween = lastTween;
    while (currentTween != null) {
      Tween previousTween = currentTween.previousTween;
      if (!currentTween.needsNextPainting()) {
        // onDone can create a new tween
        currentTween.onDone();
        // mark for deletion
        currentTween.device = null;
      }

      currentTween = previousTween;
    }

    currentTween = lastTween;
    Tween previousTween = null;
    while (currentTween != null) {
      Tween nextTween = currentTween.previousTween;
      if (currentTween.device == null) {
        if (previousTween == null) {
          lastTween = nextTween;
        } else {
          previousTween.previousTween = nextTween;
        }
      } else {
        previousTween = currentTween;
      }
      currentTween = nextTween;
    }

    if (routerPage != null) {
      for (int i = 0; i < routerPage.views.size(); i++) {
        callDidPainted(routerPage, routerPage.views.get(i));
      }
    }

    return lastTween != null;
  }

  public void callDidPainted(Page page, View view) {
    for (int i = 0; i < view.views.size(); i++) {
      callDidPainted(page, view.views.get(i));
    }
    view.didPainted(this, page);
  }

  public void unregisterTween(Tween tween) {

    if (tween == null) {
      return;
    }

    Tween currentTween = lastTween;
    Tween nextTween = null;
    while (currentTween != null) {
      if (currentTween == tween) {
        break;
      }
      nextTween = currentTween;
      currentTween = currentTween.previousTween;
    }

    if (currentTween == null) {
      // 404: not found
      return;
    }

    if (nextTween == null) {
      // unregister lastTween
      lastTween = currentTween.previousTween;
    } else {
      nextTween.previousTween = currentTween.previousTween;
    }
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (routerPage != null) {
      routerPage.handlePointerDown(pointedX, pointedY, buttonNumber);
    }
  }
}
