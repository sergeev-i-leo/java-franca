package franca.java.skia

import org.jetbrains.skia.Canvas;

import franca.java.ui.Painter
import java.awt.Color
import java.awt.Font

class SkiaPainter(private val canvas: Canvas) : Painter() {

  private val textPaint = org.jetbrains.skia.Paint().apply {
    color = 0xFF000000.toInt()
    isAntiAlias = true
  }

  private val textFont = org.jetbrains.skia.Font(
    org.jetbrains.skia.Typeface.makeFromName("Arial", org.jetbrains.skia.FontStyle.BOLD),
    48f
  )

  override fun paintText(text: String, x: Int, y: Int, painterFontName: String, deviceColor: Int, scale: Float) {
    canvas.drawString(text, x.toFloat() * scale / Painter.MULTIPLIER.toFloat(), y.toFloat() * scale / Painter.MULTIPLIER.toFloat(), textFont, textPaint)
  }
}
