package franca.java

import franca.java.ui.Device
import franca.java.ui.Painter
import org.jetbrains.skia.Font
import org.jetbrains.skia.Typeface
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

open class SkiaDevice() : Device() {

  lateinit var skiaPanel: SkiaPanel
  private var scheduler: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  override fun getTime(): Long {
    return System.nanoTime() / 1000000
  }

  override fun getWidth(): Int {
    return skiaPanel.skiaLayer.width * Painter.PRECISION;
  }

  override fun getHeight(): Int {
    return skiaPanel.skiaLayer.height * Painter.PRECISION;
  }

  override fun startPainting() {
    if (scheduler != null) {
      return
    }

    scheduler = Executors.newSingleThreadScheduledExecutor()
    scheduler?.scheduleAtFixedRate({
      val tickTime = time
      if (tickTime - lastTickTime < 16) {
        return@scheduleAtFixedRate
      }
      lastTickTime = tickTime

      if (needsPainting()) {
        SwingUtilities.invokeLater { skiaPanel.skiaLayer.needRedraw() }
      }

      if (!needsNextPainting()) {
        scheduler?.shutdown()
        scheduler = null
      }
    }, 0, 2, TimeUnit.MILLISECONDS)
  }

  open fun resolveFont(fontName: String?, fontWeight: String?, fontStyle: String?, zoomPercentage: Int?): Font {
    if (zoomPercentage == null) {
      return Font(Typeface.makeDefault(), 18f * Painter.PRECISION)
    }
    if (zoomPercentage == 100) {
      return Font(Typeface.makeDefault(), 18f * Painter.PRECISION)
    }
    return Font(Typeface.makeDefault(), 18f * zoomPercentage / 100)
  }
}
