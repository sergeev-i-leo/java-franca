package me.swift.skia_applications

import me.swift.contract.JavaDevice
import me.swift.step_gs.renderer.Page

class SkiaDevice(private val skiaTestApplication: SkiaTestApplication) : JavaDevice() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting(page: Page) {
    skiaTestApplication.startRepainting();
  }
}
