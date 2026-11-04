package franca.java

import franca.java.android_test_application.AndroidNavigator
import franca.java.ui.Device

class AndroidDevice(
  private val androidNavigator: AndroidNavigator
) : Device() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startPainting() {
    val currentView = androidNavigator.currentView

    if (currentView is AndroidDeviceView) {
      currentView.startRepainting()
    }
  }
}
