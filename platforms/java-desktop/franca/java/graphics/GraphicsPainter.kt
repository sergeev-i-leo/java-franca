package franca.java.graphics

import franca.java.ui.Painter
import java.awt.Color
import java.awt.Font
import java.awt.Graphics

class GraphicsPainter(var graphics: Graphics) : Painter() {

  override fun paintText(text: String, x: Int, y: Int, painterFontName: String, deviceColor: Int, scale: Float) {
    graphics.color = Color.BLACK
    graphics.font = Font("Arial", Font.BOLD, 48)
    graphics.drawString(text, (x.toFloat() * scale / Painter.MULTIPLIER.toFloat()).toInt(), (y.toFloat() * scale / Painter.MULTIPLIER.toFloat()).toInt())
  }
}
