package me.swift.skia_applications

import me.swift.step_gs.Device
import me.swift.step_gs.Page

class SkiaDevice(private val skiaTestApplication: SkiaTestApplication) : Device() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting(page: Page) {
    skiaTestApplication.startRepainting();
  }
}
