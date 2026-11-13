package franca.java

import franca.java.ui.Device
import franca.java.ui.Painter
import java.awt.Font
import java.awt.font.FontRenderContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities
import kotlin.math.roundToInt

open class Graphics2DDevice : Device() {

  var graphicsPanel = Graphics2DPanel(this)
  private var scheduledExecutorService: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  override fun getTime(): Long {
    return System.nanoTime() / 1000000
  }

  override fun startPainting() {
    if (scheduledExecutorService != null) {
      return
    }
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    scheduledExecutorService?.scheduleAtFixedRate(Runnable { tick() }, 0, 2, TimeUnit.MILLISECONDS)
  }

  private fun tick() {
    val tickTime = time
    if (tickTime - lastTickTime < 16) {
      return
    }
    lastTickTime = tickTime
    if (needsPainting()) {
      SwingUtilities.invokeLater { graphicsPanel.repaint() }
    }
    if (!needsNextPainting()) {
      scheduledExecutorService!!.shutdown()
      scheduledExecutorService = null
    }
  }

  open fun resolveFont(fontName: String?, fontWeight: String?, fontStyle: String?, scale: Float?): Font {
    if (scale == null) {
      return Font("SansSerif", Font.PLAIN, (18 * Painter.PRECISION).toInt())
    }
    return Font("SansSerif", Font.PLAIN, (18 * scale).toInt())
  }
}
