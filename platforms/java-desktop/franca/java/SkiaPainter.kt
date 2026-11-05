package franca.java

import franca.java.ui.Painter
import org.jetbrains.skia.*

class SkiaPainter(
  private val skiaDevice: SkiaDevice,
  private val canvas: Canvas
) : Painter() {

  override fun calculateTextWidth(fontName: String?, text: String?): Int {
    if ((fontName == null) || (text == null)) {
      return 0
    }

    val font = skiaDevice.resolveFont(fontName, null)
    return font.measureTextWidth(text).toInt()
  }

  override fun calculateFontHeight(fontName: String?): Int {
    if (fontName == null) {
      return 0
    }

    val font = skiaDevice.resolveFont(fontName, null)
    return font.metrics.height.toInt()
  }

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

  override fun paintText(text: String, x: Float, y: Float, fontName: String, deviceColor: Int, scale: Float) {
    val font = skiaDevice.resolveFont(fontName, scale)
    val textPaint = Paint().apply {
      color = 0xFF000000.toInt()
      isAntiAlias = true
    }
    canvas.drawString(text, x * scale / Painter.PRECISION, y * scale / Painter.PRECISION, font, textPaint)
  }
}
