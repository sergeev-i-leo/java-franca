package franca.java

import franca.java.ui.Device

open class JavaDesktopDevice : Device() {

  override fun getTime(): Long {
    return System.nanoTime() / 1000000
  }
}
