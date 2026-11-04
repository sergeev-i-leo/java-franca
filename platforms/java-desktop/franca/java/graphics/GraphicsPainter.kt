package franca.java.graphics

import franca.java.ui.Painter
import java.awt.Color
import java.awt.Font
import java.awt.Graphics


class GraphicsPainter(private var graphics: Graphics) : Painter() {

  override fun fillCircle(x: Float, y: Float, radius: Float, deviceColor: Int, scale: Float) {
    graphics.color = Color(deviceColor, true)

    val px = (x * scale / PRECISION).toInt()
    val py = (y * scale / PRECISION).toInt()
    val pr = (radius * scale / PRECISION).toInt()

    graphics.fillOval(px - pr, py - pr, pr * 2, pr * 2)
  }

  override fun paintText(text: String, x: Float, y: Float, painterFontName: String, deviceColor: Int, scale: Float) {
    graphics.color = Color.BLACK
    graphics.font = Font("Arial", Font.BOLD, 48)
    graphics.drawString(text, (x * scale / Painter.PRECISION).toInt(), (y * scale / Painter.PRECISION).toInt())
  }
}
