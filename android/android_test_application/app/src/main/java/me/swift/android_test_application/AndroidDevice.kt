package me.swift.android_test_application

import me.swift.engine.Device
import me.swift.engine.Page

class AndroidDevice(
  private val androidNavigator: AndroidNavigator
) : Device() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting(page: Page) {
    val currentView = androidNavigator.currentView

    if ((currentView is AndroidDeviceView) && (currentView.getPage() === page)) {
      currentView.startRepainting()
    }
  }
}