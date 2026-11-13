package franca.java

import franca.java.ui.Painter
import org.jetbrains.skia.*

class SkiaPainter(
  private val skiaDevice: SkiaDevice,
  private val canvas: Canvas
) : Painter() {

  override fun getWidth(): Int {
    return skiaDevice.skiaLayer.width * PRECISION;
  }

  override fun getHeight(): Int {
    return skiaDevice.skiaLayer.height * PRECISION;
  }

  override fun calculateTextWidth(text: String?, fontName: String?, fontWeight: String?, fontStyle: String?): Int {
    if (text == null) {
      return 0
    }

    var currentFontName = fontName
    var currentFontWeight = fontWeight
    var currentFontStyle = fontName;
    if (currentTextStyle != null) {
      if (currentFontName == null) {
        currentFontName = currentTextStyle.fontName
      }
      if (currentFontWeight == null) {
        currentFontWeight = currentTextStyle.fontWeight
      }
      if (currentFontStyle == null) {
        currentFontStyle = currentTextStyle.fontStyle
      }
    }

    val font = skiaDevice.resolveFont(currentFontName, currentFontWeight, currentFontStyle, null)
    return font.measureTextWidth(text).toInt()
  }

  override fun calculateFontHeight(fontName: String?, fontWeight: String?, fontStyle: String?): Int {
    var currentFontName = fontName
    var currentFontWeight = fontWeight
    var currentFontStyle = fontName;
    if (currentTextStyle != null) {
      if (currentFontName == null) {
        currentFontName = currentTextStyle.fontName
      }
      if (currentFontWeight == null) {
        currentFontWeight = currentTextStyle.fontWeight
      }
      if (currentFontStyle == null) {
        currentFontStyle = currentTextStyle.fontStyle
      }
    }

    val font = skiaDevice.resolveFont(currentFontName, currentFontWeight, currentFontStyle, null)
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
      (currentOpacity shl 24) or (color and 0x00FFFFFF)
    }
    paint.color = finalColor
    paint.isAntiAlias = true

    canvas.drawCircle(px, py, pr, paint)
  }

  override fun paintText(text: String, x: Int, y: Int, fontName: String?, fontWeight: String?, fontStyle: String?, color: Int, scale: Float) {
    var currentFontName = fontName
    var currentFontWeight = fontWeight
    var currentFontStyle = fontName;
    if (currentTextStyle != null) {
      if (currentFontName == null) {
        currentFontName = currentTextStyle.fontName
      }
      if (currentFontWeight == null) {
        currentFontWeight = currentTextStyle.fontWeight
      }
      if (currentFontStyle == null) {
        currentFontStyle = currentTextStyle.fontStyle
      }
    }
    val font = skiaDevice.resolveFont(currentFontName, currentFontWeight, currentFontStyle, scale)

    val withOpacity = ((color shr 24) and 0xFF) != 0
    val finalColor = if (withOpacity) {
      color
    } else {
      (currentOpacity shl 24) or (color and 0x00FFFFFF)
    }
    val textPaint = Paint().apply {
      this.color = finalColor
      isAntiAlias = true
    }
    val metrics = font.metrics
    val baselineY = (y * scale / PRECISION - metrics.ascent)
    canvas.drawString(text, x * scale / PRECISION, baselineY, font, textPaint)
  }
}
