package franca.java.ui;

import franca.java.Callback;
import franca.java.TranspilableClass;
import franca.java.ui.animations.Animation;

/**
 * Device manages all animations.
 * Ownership rule: whoever creates the animation (via registerAnimation) does NOT own it, Device owns it.
 * Page calls destroy() when removing animation from the chain.
 * External code must NEVER call destroy() on animations directly.
 */

public class Device extends TranspilableClass {

  public Router router = null;

  // animation orchestration
  public Animation lastAnimation = null;

  public boolean isReady = false;

  public void getReady(Callback<Integer> callback) {
    isReady = true;
    callback.onResult(200);
  }

  public long getTime() {
    return 0L;
  }

  public int getWidth() {
    return 0;
  }

  public int getHeight() {
    return 0;
  }

  public void requestPainting() {
    // one-shot animation
    Animation animation = new Animation(this, 0, 0, 0L);
    registerAnimation(animation);
  }

  public void registerAnimation(Animation animation) {
    animation.previousAnimation = lastAnimation;
    lastAnimation = animation;

    animation.registeredTime = getTime();

    startPainting();
  }

  public void startPainting() {
    // real painting
  }

  public boolean needsPainting() {

    Animation currentAnimation = lastAnimation;
    if (currentAnimation == null) {
      return false;
    }

    while (currentAnimation != null) {
      currentAnimation.tick();
      currentAnimation = currentAnimation.previousAnimation;
    }

    return true;
  }

  public void paint(Painter painter) {
    if (router != null) {
      router.paint(painter);
    }
  }

  public boolean needsNextPainting() {

    //  mark for deletion

    Animation currentAnimation = lastAnimation;
    while (currentAnimation != null) {
      Animation previousAnimation = currentAnimation.previousAnimation;
      if (!currentAnimation.needsNextPainting()) {
        // onDone can create a new tween
        currentAnimation.onDone();
        // mark for deletion
        currentAnimation.device = null;
      }

      currentAnimation = previousAnimation;
    }

    currentAnimation = lastAnimation;
    Animation previousAnimation = null;
    while (currentAnimation != null) {
      Animation nextAnimation = currentAnimation.previousAnimation;
      if (currentAnimation.device == null) {
        if (previousAnimation == null) {
          lastAnimation = nextAnimation;
        } else {
          previousAnimation.previousAnimation = nextAnimation;
        }
      } else {
        previousAnimation = currentAnimation;
      }
      currentAnimation = nextAnimation;
    }

    if (router != null) {
      router.didPainted();
    }

    return lastAnimation != null;
  }

  public void abortAnimation(Animation animation) {

    if (animation == null) {
      return;
    }

    Animation currentAnimation = lastAnimation;
    Animation nextAnimation = null;
    while (currentAnimation != null) {
      if (currentAnimation == animation) {
        break;
      }
      nextAnimation = currentAnimation;
      currentAnimation = currentAnimation.previousAnimation;
    }

    if (currentAnimation == null) {
      // 404: not found
      return;
    }

    if (nextAnimation == null) {
      // unregister lastTween
      lastAnimation = currentAnimation.previousAnimation;
    } else {
      nextAnimation.previousAnimation = currentAnimation.previousAnimation;
    }
  }

  public void handlePointerDown(int pointedX, int pointedY, int buttonNumber) {
    if (router != null) {
      router.handlePointerDown(pointedX, pointedY, buttonNumber);
    }
  }
}
