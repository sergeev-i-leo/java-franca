package me.swift.skia_applications

import me.swift.engine.Device
import me.swift.engine.Page

class SkiaDevice(private val skiaTestApplication: SkiaTestApplication) : Device() {

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting(page: Page) {
    skiaTestApplication.startRepainting();
  }
}
