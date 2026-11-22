package franca.java

import franca.java.ui.Device
import franca.java.ui.Painter
import java.awt.Font
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

open class Graphics2DDevice() : Device() {

  lateinit var graphics2DPanel: Graphics2DPanel
  private var scheduledExecutorService: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  override fun getTime(): Long {
    return System.nanoTime() / 1000000
  }

  override fun getWidth(): Int {
    return graphics2DPanel.width * Painter.PRECISION;
  }

  override fun getHeight(): Int {
    return graphics2DPanel.height * Painter.PRECISION;
  }

  override fun startPainting() {
    if (scheduledExecutorService != null) {
      return
    }
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    scheduledExecutorService?.scheduleAtFixedRate({ tick() }, 0, 2, TimeUnit.MILLISECONDS)
  }

  private fun tick() {
    val tickTime = time
    if (tickTime - lastTickTime < 16) {
      return
    }
    lastTickTime = tickTime
    if (needsPainting()) {
      SwingUtilities.invokeLater {
        graphics2DPanel.repaint()
      }
    }
    if (!needsNextPainting()) {
      scheduledExecutorService!!.shutdown()
      scheduledExecutorService = null
    }
  }

  open fun resolveFont(fontName: String?, fontWeight: String?, fontStyle: String?, scale: Float?): Font {
    if (scale == null) {
      return Font("SansSerif", Font.PLAIN, 18 * Painter.PRECISION)
    }
    return Font("SansSerif", Font.PLAIN, (18 * scale).toInt())
  }
}
