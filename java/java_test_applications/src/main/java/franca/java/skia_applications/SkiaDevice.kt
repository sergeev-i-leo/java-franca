package franca.java.skia_applications

import franca.java.contracted.JavaDevice
import franca.java.step_gs.renderer.Page

class SkiaDevice(private val skiaTestApplication: SkiaTestApplication) : JavaDevice() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting(page: Page) {
    skiaTestApplication.startRepainting();
  }
}
