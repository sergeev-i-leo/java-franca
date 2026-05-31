package me.swift.step_gs.animations;

import me.swift.step_gs.Page;
import me.swift.engine.TranspilableClass;
import me.swift.step_gs.Device;

public class Animation extends TranspilableClass {

  public float initialValue = 0f;
  public float currentValue = 0f;
  public float targetValue = 0f;

  public long duration = 0L;

  public int animationId = 0;
  public long registeredTime = 0L;
  public Animation previousAnimation = null;
  public Animation nextAnimation = null;

  public Animation(float initialValue, float targetValue, long duration) {
    this.initialValue = initialValue;
    this.currentValue = this.initialValue;
    this.targetValue = targetValue;

    this.duration = duration;
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  public boolean isTicking() {
    return duration > 0L;
  }

  public boolean needsRepainting(Device device, Page page, long time) {
    return duration > 0L;
  }

  public boolean needsNextRepainting(Device device, Page page, long time) {
    return duration > 0L;
  }

}

