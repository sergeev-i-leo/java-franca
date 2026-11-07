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

  override fun fillCircle(x: Int, y: Int, radius: Int, color: Int) {
    val px = x.toFloat() / PRECISION.toFloat()
    val py = y.toFloat() / PRECISION.toFloat()
    val pr = radius.toFloat() / PRECISION.toFloat()

    val paint = Paint()
    paint.mode = PaintMode.FILL

    val withOpacity = ((color shr 24) and 0xFF) != 0
    val finalColor = if (withOpacity) {
      color
    } else {
      (opacity shl 24) or (color and 0x00FFFFFF)
    }
    paint.color = finalColor
    paint.isAntiAlias = true

    canvas.drawCircle(px, py, pr, paint)
  }

  override fun paintText(text: String, x: Int, y: Int, fontName: String, color: Int, scale: Float) {
    val font = skiaDevice.resolveFont(fontName, scale)
    val withOpacity = ((color shr 24) and 0xFF) != 0
    val finalColor = if (withOpacity) {
      color
    } else {
      (opacity shl 24) or (color and 0x00FFFFFF)
    }
    val textPaint = Paint().apply {
      this.color = finalColor
      isAntiAlias = true
    }
    canvas.drawString(text, x * scale / PRECISION, y * scale / PRECISION, font, textPaint)
  }
}
