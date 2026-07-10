package franca.java.android_test_application

import franca.java.AndroidRouterView
import franca.java.graphics.device.Router

class AndroidRouter(
  private val androidNavigator: AndroidNavigator
) : Router() {

  override fun startRepainting() {
    val currentView = androidNavigator.currentView

    if (currentView is AndroidRouterView) {
      currentView.startRepainting()
    }
  }
}