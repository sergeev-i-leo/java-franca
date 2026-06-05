package me.swift.swing_applications;

import me.swift.contract.JavaDevice;
import me.swift.step_gs.renderer.Page;

public class SwingDevice extends JavaDevice {

  SwingApplication swingApplication;

  public SwingDevice(SwingApplication swingApplication) {
    super();

    this.swingApplication = swingApplication;
  }

  @Override
  public long getTime() {
    return System.nanoTime() / 1_000_000;
  }

  @Override
  public void startRepainting(Page page) {
    swingApplication.startRepainting();
  }
}
