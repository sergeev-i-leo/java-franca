package franca.java

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class Graphics2DPanel(
  var graphicsDevice: Graphics2DDevice
) : JPanel() {

  init {
    background = Color.WHITE
    addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          graphicsDevice.handlePointerDown(mouseEvent.x, mouseEvent.y, 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          graphicsDevice.handlePointerDown(mouseEvent.x, mouseEvent.y, 3)
        }
      }
    })
  }

  override fun paintComponent(graphics: Graphics) {
    super.paintComponent(graphics)
    val graphicsPainter = Graphics2DPainter(graphicsDevice, graphics as Graphics2D)
    graphicsDevice.paint(graphicsPainter)
    graphicsDevice.startPainting();
  }
}
