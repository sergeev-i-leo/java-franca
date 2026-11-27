package franca.java

import franca.java.ui.Painter
import org.jetbrains.skia.*

class SkiaPainter(
  private val skiaDevice: SkiaDevice,
) : Painter() {

  lateinit var canvas: Canvas

  fun assignCanvas(canvas: Canvas) {
    this.canvas = canvas
  }

  override fun calculateTextWidth(text: String?, fontName: String?, fontWeight: String?, fontStyle: String?): Int {
    if (text == null) {
      return 0
    }

    var resolvedFontName = fontName
    var resolvedFontWeight = fontWeight
    var resolvedFontStyle = fontName;
    if (currentTextStyle != null) {
      if (resolvedFontName == null) {
        resolvedFontName = currentTextStyle.fontName
      }
      if (resolvedFontWeight == null) {
        resolvedFontWeight = currentTextStyle.fontWeight
      }
      if (resolvedFontStyle == null) {
        resolvedFontStyle = currentTextStyle.fontStyle
      }
    }

    val font = skiaDevice.resolveFont(resolvedFontName, resolvedFontWeight, resolvedFontStyle, null)
    return font.measureTextWidth(text).toInt()
  }

  override fun calculateFontHeight(fontName: String?, fontWeight: String?, fontStyle: String?): Int {
    var resolvedFontName = fontName
    var resolvedFontWeight = fontWeight
    var resolvedFontStyle = fontName;
    if (currentTextStyle != null) {
      if (resolvedFontName == null) {
        resolvedFontName = currentTextStyle.fontName
      }
      if (resolvedFontWeight == null) {
        resolvedFontWeight = currentTextStyle.fontWeight
      }
      if (resolvedFontStyle == null) {
        resolvedFontStyle = currentTextStyle.fontStyle
      }
    }

    val font = skiaDevice.resolveFont(resolvedFontName, resolvedFontWeight, resolvedFontStyle, null)
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

  override fun paintText(text: String, x: Int, y: Int, fontName: String?, fontWeight: String?, fontStyle: String?, color: Int, zoomPercentage: Int) {
    var resolvedFontName = fontName
    var resolvedFontWeight = fontWeight
    var resolvedFontStyle = fontName;
    if (currentTextStyle != null) {
      if (resolvedFontName == null) {
        resolvedFontName = currentTextStyle.fontName
      }
      if (resolvedFontWeight == null) {
        resolvedFontWeight = currentTextStyle.fontWeight
      }
      if (resolvedFontStyle == null) {
        resolvedFontStyle = currentTextStyle.fontStyle
      }
    }
    val font = skiaDevice.resolveFont(resolvedFontName, resolvedFontWeight, resolvedFontStyle, zoomPercentage)

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
    val baselineY = (y * zoomPercentage / (PRECISION * 100) - metrics.ascent)
    canvas.drawString(text, (x * zoomPercentage / (PRECISION * 100)).toFloat(), baselineY, font, textPaint)
  }
}
