package franca.java.graphics

import franca.java.JavaDesktopRouter
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.JPanel
import javax.swing.SwingUtilities

class GraphicsRouter : JavaDesktopRouter() {
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

class GraphicsPanel(var graphicsRouter: GraphicsRouter) : JPanel() {
  init {
    background = Color.WHITE
    addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          graphicsRouter.handlePointerDown(mouseEvent.x, mouseEvent.y, 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          graphicsRouter.handlePointerDown(mouseEvent.x, mouseEvent.y, 3)
        }
      }
    })
  }

  override fun paintComponent(graphics: Graphics) {
    super.paintComponent(graphics)
    val graphicsPainter = GraphicsPainter(graphics)
    graphicsRouter.paint(graphicsPainter)
    graphicsRouter.startPainting();
  }
}
