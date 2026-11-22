package franca.java

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class Graphics2DPanel(
  var graphics2DDevice: Graphics2DDevice
) : JPanel() {

  init {
    layout = BorderLayout()
    background = Color.WHITE

    addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          graphics2DDevice.handlePointerDown(mouseEvent.x, mouseEvent.y, 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          graphics2DDevice.handlePointerDown(mouseEvent.x, mouseEvent.y, 3)
        }
      }
    })
  }

  override fun paintComponent(graphics: Graphics) {
    super.paintComponent(graphics)
    val graphicsPainter = Graphics2DPainter(graphics2DDevice)
    graphicsPainter.assignGraphics2D(graphics as Graphics2D)
    graphics2DDevice.paint(graphicsPainter)
    graphics2DDevice.startPainting();
  }
}
