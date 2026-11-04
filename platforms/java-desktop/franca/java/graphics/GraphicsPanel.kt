package franca.java.graphics

import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class GraphicsPanel(
  var graphicsDevice: GraphicsDevice
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
    val graphicsPainter = GraphicsPainter(graphicsDevice, graphics)
    graphicsDevice.paint(graphicsPainter)
    graphicsDevice.startPainting();
  }
}
