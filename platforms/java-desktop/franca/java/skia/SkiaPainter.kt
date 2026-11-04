package franca.java.skia

import org.jetbrains.skia.Canvas;

import franca.java.ui.Painter

class SkiaPainter(private val canvas: Canvas) : Painter() {

  private val textPaint = org.jetbrains.skia.Paint().apply {
    color = 0xFF000000.toInt()
    isAntiAlias = true
  }

  private val textFont = org.jetbrains.skia.Font(
    org.jetbrains.skia.Typeface.makeFromName("Arial", org.jetbrains.skia.FontStyle.BOLD),
    48f
  )

  override fun fillCircle(x: Float, y: Float, radius: Float, deviceColor: Int, scale: Float) {

  }

  override fun paintText(text: String, x: Float, y: Float, painterFontName: String, deviceColor: Int, scale: Float) {
    canvas.drawString(text, x.toFloat() * scale / Painter.PRECISION.toFloat(), y.toFloat() * scale / Painter.PRECISION.toFloat(), textFont, textPaint)
  }
}
