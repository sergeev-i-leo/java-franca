package me.swift.swing_applications;

import me.swift.engine.Device;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SwingDevice extends Device {

  SwingTestApplication swingTestApplication;

  ScheduledExecutorService scheduledExecutorService = null;
  long lastTickTime = 0L;

  public SwingDevice(SwingTestApplication swingTestApplication) {
    super();

    this.swingTestApplication = swingTestApplication;
  }

  @Override
  public long getTime() {
    return System.nanoTime() / 1_000_000;
  }

  @Override
  public void startRepainting() {
    if (scheduledExecutorService != null) {
      return;
    }
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    lastTickTime = getTime();
    scheduledExecutorService.scheduleAtFixedRate(this::tick, 0, 2, TimeUnit.MILLISECONDS);
  }

  private void tick() {
    long tickTime = getTime();
    if (tickTime - lastTickTime < 16) {
      return;
    }
    lastTickTime = tickTime;
    if (swingTestApplication.page.needsRepainting()) {
      SwingUtilities.invokeLater(() -> swingTestApplication.repaint());
    }
    if (!swingTestApplication.page.needsNextRepainting()) {
      scheduledExecutorService.shutdown();
      scheduledExecutorService = null;
    }
  }
}
