package me.swift.engine;

import me.swift.engine.animations.Animation;
import me.swift.engine.expected.ExpectedList;
import me.swift.engine.painter.Painter;

/**
 * Page manages all animations.
 * Ownership rule: whoever creates the animation (via registerAnimation)
 * does NOT own it — Page owns it.
 * Page calls destroy() when removing animation from the chain.
 * External code must NEVER call destroy() on animations directly.
 */

public class Page extends TranspilableClass {

  Device device;

  int lastAnimationId = 0;
  Animation firstAnimation = null;

  public ExpectedList<View> views = new ExpectedList<View>();

  public Page(Device device) {
    this.device = device;
  }

  @Override
  public void destroy() {
    views.destroyAll();
    views.destroy();
    super.destroy();
  }

  public synchronized void requestRepainting() {
    registerAnimation(new Animation(0f, 0f, 1L));
  }

  public synchronized void registerAnimation(Animation animation) {
    // the new animation becomes the first in the chain because registerAnimation can be called from Animation.needsRepainting

    lastAnimationId++;
    animation.animationId = lastAnimationId;
    animation.registeredTime = device.getTime();

    animation.nextAnimation = firstAnimation;
    if (firstAnimation != null) {
      firstAnimation.previousAnimation = animation;
    }
    firstAnimation = animation;

    device.startRepainting(this);
  }

  public synchronized void removeAnimation(Animation animation) {

    Animation currentAnimation = firstAnimation;
    while (currentAnimation != null) {
      if (currentAnimation.animationId == animation.animationId) {
        Animation previousAnimation = currentAnimation.previousAnimation;
        Animation nextAnimation = currentAnimation.nextAnimation;

        if (previousAnimation != null) {
          previousAnimation.nextAnimation = nextAnimation;
        }
        if (nextAnimation != null) {
          nextAnimation.previousAnimation = previousAnimation;
        }
        if (currentAnimation == firstAnimation) {
          firstAnimation = nextAnimation;
        }
        break;
      }
      currentAnimation = currentAnimation.nextAnimation;
    }
    if (firstAnimation == null) {
      lastAnimationId = 0;
    }
  }

  public synchronized boolean needsRepainting() {
    boolean result = false;

    long time = device.getTime();

    Animation currentAnimation = firstAnimation;
    while (currentAnimation != null) {
      result = currentAnimation.needsRepainting(device, this, time) || result;
      if (currentAnimation.duration == 1L) {
        // it's one-shot animation
        Animation previousAnimation = currentAnimation.previousAnimation;
        Animation nextAnimation = currentAnimation.nextAnimation;
        if (previousAnimation != null) {
          previousAnimation.nextAnimation = nextAnimation;
        }
        if (nextAnimation != null) {
          nextAnimation.previousAnimation = previousAnimation;
        }
        if (currentAnimation.animationId == firstAnimation.animationId) {
          firstAnimation = nextAnimation;
        }
        currentAnimation.destroy();
        currentAnimation = nextAnimation;
      } else {
        currentAnimation = currentAnimation.nextAnimation;
      }
    }
    return result;
  }

  public void paint(Painter painter) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).paint(device, painter, this);
    }
  }

  public synchronized boolean needsNextRepainting() {
    boolean result = false;

    long time = device.getTime();

    Animation currentAnimation = firstAnimation;
    while (currentAnimation != null) {
      if (currentAnimation.duration == 1L) {
        // wow! one-shot animation has been created during paint(), welcome to next needsRepainting
        result = true;
      } else {
        // always run needsNextRepainting
        result = currentAnimation.needsNextRepainting(device, this, time) || result;
      }
      currentAnimation = currentAnimation.nextAnimation;
    }
    return result;
  }

  public void handlePointerDown(float pointedX, float pointedY, int buttonNumber) {
    for (int i = 0; i < views.size(); i++) {
      views.get(i).handlePointerDown(device, this, 0f, 0f, pointedX, pointedY, buttonNumber);
    }
  }
}
