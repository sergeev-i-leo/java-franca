package franca.java

import franca.java.ui.Painter
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints

class Graphics2DPainter(
  private val graphicsDevice: Graphics2DDevice,
  private var graphics2D: Graphics2D
) : Painter() {

  init {
    graphics2D.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    )
  }

  override fun calculateTextWidth(fontName: String?, text: String?): Int {
    if ((fontName == null) || (text == null)) {
      return 0
    }

    val font = graphicsDevice.resolveFont(fontName, null)
    val metrics = graphics2D.getFontMetrics(font)
    return metrics.stringWidth(text)
  }

  override fun calculateFontHeight(fontName: String?): Int {
    if (fontName == null) {
      return 0
    }

    val font = graphicsDevice.resolveFont(fontName, null)
    val metrics = graphics2D.getFontMetrics(font)
    return metrics.height
  }

  override fun fillCircle(x: Float, y: Float, radius: Float, deviceColor: Int, scale: Float) {
    graphics2D.color = Color(deviceColor, true)

    val px = (x * scale / PRECISION).toInt()
    val py = (y * scale / PRECISION).toInt()
    val pr = (radius * scale / PRECISION).toInt()

    graphics2D.fillOval(px - pr, py - pr, pr * 2, pr * 2)
  }

  override fun paintText(text: String, x: Float, y: Float, fontName: String, deviceColor: Int, scale: Float) {
    graphics2D.color = Color.BLACK
    graphics2D.font = Font("Arial", Font.BOLD, 48)
    graphics2D.drawString(text, (x * scale / Painter.PRECISION).toInt(), (y * scale / Painter.PRECISION).toInt())
  }
}
