package franca.java.skia

import franca.java.ui.Painter
import org.jetbrains.skia.Paint;
import org.jetbrains.skia.PaintMode;
import org.jetbrains.skia.Canvas;
import org.jetbrains.skia.Color;

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
    val px = x * scale / Painter.PRECISION
    val py = y * scale / Painter.PRECISION
    val pr = radius * scale / Painter.PRECISION

    val paint = Paint()
    paint.mode = PaintMode.FILL
    paint.color = deviceColor
    paint.isAntiAlias = true

    canvas.drawCircle(px, py, pr, paint)
  }

  override fun paintText(text: String, x: Float, y: Float, painterFontName: String, deviceColor: Int, scale: Float) {
    canvas.drawString(text, x.toFloat() * scale / Painter.PRECISION.toFloat(), y.toFloat() * scale / Painter.PRECISION.toFloat(), textFont, textPaint)
  }
}
