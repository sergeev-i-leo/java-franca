package franca.java

import franca.java.ui.Router

open class JavaDesktopRouter : Router() {

  override fun getTime(): Long {
    return System.nanoTime() / 1000000
  }
}
