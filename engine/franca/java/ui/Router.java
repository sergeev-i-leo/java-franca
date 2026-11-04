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

  public Tween lastTween = null;

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
    if (lastTween == null) {
      lastTween = tween;
      return;
    }

    tween.previousTween = lastTween;
    lastTween = tween;

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
    if (topPage != null) {
      topPage.paint(painter);
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
        currentTween.router = null;
      }

      currentTween = previousTween;
    }

    currentTween = lastTween;
    while (currentTween != null) {
      if (currentTween.router == null) {
        unregisterTween(currentTween);
        currentTween = lastTween;
      } else {
        currentTween = currentTween.previousTween;
      }
    }

    return lastTween != null;
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
    if (topPage != null) {
      topPage.handlePointerDown(pointedX, pointedY, buttonNumber);
    }
  }
}
