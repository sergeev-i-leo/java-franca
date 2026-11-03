package franca.java

import franca.java.android_test_application.AndroidNavigator
import franca.java.ui.Router

class AndroidRouter(
  private val androidNavigator: AndroidNavigator
) : Router() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startPainting() {
    val currentView = androidNavigator.currentView

    if (currentView is AndroidRouterView) {
      currentView.startRepainting()
    }
  }
}
