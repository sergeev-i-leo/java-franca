package me.swift.skia_applications

import org.jetbrains.skiko.SkiaLayer
import me.swift.engine.Device
import me.swift.engine.Page
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

class SkiaDevice(private val skiaLayer: SkiaLayer) : Device() {

  private var scheduler: ScheduledExecutorService? = null
  private var lastTickTime = 0L
  private var page: Page? = null

  fun setPage(page: Page) {
    this.page = page
  }

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting() {
    if (scheduler != null) {
      return
    }

    scheduler = Executors.newSingleThreadScheduledExecutor()
    lastTickTime = time
    scheduler?.scheduleAtFixedRate({
      val tickTime = time
      if (tickTime - lastTickTime < 16) {
        return@scheduleAtFixedRate
      }
      lastTickTime = tickTime

      val page = this.page ?: return@scheduleAtFixedRate

      if (page.needsRepainting()) {
        SwingUtilities.invokeLater { skiaLayer.needRedraw() }
      }

      if (!page.needsNextRepainting()) {
        scheduler?.shutdown()
        scheduler = null
      }
    }, 0, 2, TimeUnit.MILLISECONDS)
  }
}
