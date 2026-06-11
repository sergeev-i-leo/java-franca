package franca.java.android_test_application

import franca.java.graphics.device.Device

class AndroidDevice(
  private val androidNavigator: AndroidNavigator
) : Device() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting() {
    val currentView = androidNavigator.currentView

    if (currentView is AndroidDeviceView) {
      currentView.startRepainting()
    }
  }
}