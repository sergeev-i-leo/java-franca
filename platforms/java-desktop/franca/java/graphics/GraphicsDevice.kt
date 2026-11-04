package franca.java.graphics

import franca.java.JavaDesktopDevice
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

class GraphicsDevice : JavaDesktopDevice() {
  var graphicsPanel = GraphicsPanel(this)
  private var scheduledExecutorService: ScheduledExecutorService? = null
  private var lastTickTime = 0L

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
