package me.swift.swing_applications;

import me.swift.step_gs.Device;
import me.swift.step_gs.Page;

public class SwingDevice extends Device {

  SwingTestApplication swingTestApplication;

  public SwingDevice(SwingTestApplication swingTestApplication) {
    super();

    this.swingTestApplication = swingTestApplication;
  }

  @Override
  public long getTime() {
    return System.nanoTime() / 1_000_000;
  }

  @Override
  public void startRepainting(Page page) {
    swingTestApplication.startRepainting();
  }
}
