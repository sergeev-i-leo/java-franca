package franca.java.expected;

import franca.java.graphics.Page;
import franca.java.graphics.animations.Tween;

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

  public void readFile(String path, StringConsumer callback) {
  }

  public void writeFile(String path, String content, IntegerConsumer callback) {
  }

  public Page getTopPage() {
    return topPage;
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

  public void paint(Painter painter) {
    if (topPage != null) {
      topPage.paint(painter);
    }
  }

  public void handlePointerDown(float pointedX, float pointedY, int buttonNumber) {
    if (topPage != null) {
      topPage.handlePointerDown(pointedX, pointedY, buttonNumber);
    }
  }

  public void requestRepainting() {
    // one-shot animation
    Tween tween = new Tween(null, null, 0L, 0);
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

    startRepainting();
  }

  public void startRepainting() {
  }

  public void removeTween(Tween tween) {

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

  public boolean needsRepainting() {

    boolean result = false;

    Tween currentTween = firstTween;
    while (currentTween != null) {
      result = currentTween.needsRepainting(this) || result;
      if (currentTween.getEase() == null) {
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
        currentTween = nextTween;
      } else {
        currentTween = currentTween.nextTween;
      }
    }
    return result;
  }

  public boolean needsNextRepainting() {

    boolean result = false;

    Tween currentTween = firstTween;
    while (currentTween != null) {
      if (currentTween.getEase() == null) {
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
