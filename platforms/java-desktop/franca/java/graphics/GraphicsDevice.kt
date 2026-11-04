package franca.java.graphics

import franca.java.Callback
import franca.java.ui.Device
import franca.java.ui.Painter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

class GraphicsDevice : Device() {

  var graphicsPanel = GraphicsPanel(this)
  private var scheduledExecutorService: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  override fun init(callback: Callback<Int?>) {
    callback.onResult(200)
  }

  override fun getTime(): Long {
    return System.nanoTime() / 1000000
  }

  override fun getWidth(): Float {
    return graphicsPanel.width.toFloat() * Painter.PRECISION;
  }

  override fun getHeight(): Float {
    return graphicsPanel.height.toFloat() * Painter.PRECISION;
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
}
