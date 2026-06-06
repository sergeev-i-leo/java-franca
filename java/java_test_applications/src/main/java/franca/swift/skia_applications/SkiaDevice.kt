package franca.swift.skia_applications

import franca.swift.contract.JavaDevice
import franca.swift.step_gs.renderer.Page

class SkiaDevice(private val skiaTestApplication: SkiaTestApplication) : JavaDevice() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting(page: Page) {
    skiaTestApplication.startRepainting();
  }
}
