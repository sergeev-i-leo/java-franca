package franca.java.skia

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

    val font = resolveFont(fontName, null)
    return (font.measureTextWidth(text) * Painter.PRECISION).toInt()
  }

  override fun calculateFontHeight(fontName: String?): Int {
    if (fontName == null) {
      return 0
    }

    val font = resolveFont(fontName, null)
    return (font.metrics.height * Painter.PRECISION).toInt()
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
    val font = resolveFont(fontName, scale)
    val textPaint = Paint().apply {
      color = 0xFF000000.toInt()
      isAntiAlias = true
    }
    canvas.drawString(text, x * scale / Painter.PRECISION, y * scale / Painter.PRECISION, font, textPaint)
  }

  private fun resolveFont(fontName: String? , scale: Float?): Font {
    val typeface: Typeface
    val multiplier: Float

    when (fontName) {
      "primary, large" -> {
        typeface = skiaDevice.primaryTypeface
        multiplier = 1.25f
      }
      "secondary, h1" -> {
        typeface = skiaDevice.secondaryTypeface
        multiplier = 2f
      }
      "secondary, h2" -> {
        typeface = skiaDevice.secondaryTypeface
        multiplier = 1.75f
      }
      "secondary, h3" -> {
        typeface = skiaDevice.secondaryTypeface
        multiplier = 1.5f
      }
      "secondary, h4" -> { typeface = skiaDevice.secondaryTypeface
        multiplier = 1.25f
      }
      "secondary, h5" -> {
        typeface = skiaDevice.secondaryTypeface
        multiplier = 1f
      }
      "secondary, h6" -> {
        typeface = skiaDevice.secondaryTypeface
        multiplier = 0.75f
      }
      "tertiary" -> {
        typeface = skiaDevice.tertiaryTypeface
        multiplier = 1f
      }
      "tertiary, large" -> {
        typeface = skiaDevice.tertiaryTypeface
        multiplier = 1.25f
      }
      "condensed" -> {
        typeface = skiaDevice.condensedTypeface
        multiplier = 1f
      }
      "condensed, large" -> {
        typeface = skiaDevice.condensedTypeface
        multiplier = 1.25f
      }
      else -> {
        typeface = skiaDevice.primaryTypeface
        multiplier = 1f
      }
    }

    val size = if (scale == null) {
      skiaDevice.fontSize * multiplier
    } else {
      (skiaDevice.fontSize * multiplier * scale) / Painter.PRECISION
    }
    return Font(typeface, size)

  }
}
