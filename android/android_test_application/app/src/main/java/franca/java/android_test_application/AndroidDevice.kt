package me.swift.android_test_application

import me.swift.step_gs.Page
import me.swift.step_gs.contract.Device


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