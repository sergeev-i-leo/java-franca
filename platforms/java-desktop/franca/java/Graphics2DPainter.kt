package franca.java

import franca.java.ui.Painter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints

open class Graphics2DPainter(
  val graphics2DDevice: Graphics2DDevice,
) : Painter() {

  lateinit var graphics2D: Graphics2D

  open fun assignGraphics2D(graphics2D: Graphics2D) {
    this.graphics2D = graphics2D
    this.graphics2D.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    )
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

    val font = graphics2DDevice.resolveFont(resolvedFontName, resolvedFontWeight, resolvedFontStyle, null)
    val metrics = graphics2D.getFontMetrics(font)
    return metrics.stringWidth(text)
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

    val font = graphics2DDevice.resolveFont(resolvedFontName, resolvedFontWeight, resolvedFontStyle, null)
    val metrics = graphics2D.getFontMetrics(font)
    return metrics.height
  }

  override fun fillCircle(x: Int, y: Int, radius: Int, color: Int) {
    val withOpacity = ((color shr 24) and 0xFF) != 0
    val finalColor = if (withOpacity) {
      color
    } else {
      (currentOpacity shl 24) or (color and 0x00FFFFFF)
    }
    graphics2D.color = Color(finalColor, true)

    val px = (x.toFloat() / PRECISION.toFloat()).toInt()
    val py = (y.toFloat() / PRECISION.toFloat()).toInt()
    val pr = (radius.toFloat() / PRECISION.toFloat()).toInt()

    graphics2D.fillOval(px - pr, py - pr, pr * 2, pr * 2)
  }

  override fun paintText(text: String, x: Int, y: Int, fontName: String?, fontWeight: String?, fontStyle: String?, color: Int, scale: Float) {
    var resolvedFontName = fontName
    var resolvedFontWeight = fontWeight
    var resolvedFontStyle = fontStyle
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
    val resolvedFont = graphics2DDevice.resolveFont(resolvedFontName, resolvedFontWeight, resolvedFontStyle, scale)
    graphics2D.font = resolvedFont
    val metrics = graphics2D.getFontMetrics(resolvedFont)
    val resolvedY = (y * scale / PRECISION + metrics.ascent).toInt()

    val withOpacity = ((color shr 24) and 0xFF) != 0
    val finalColor = if (withOpacity) {
      color
    } else {
      (currentOpacity shl 24) or (color and 0x00FFFFFF)
    }
    graphics2D.color = Color(finalColor, true)

    graphics2D.drawString(text, (x * scale / PRECISION).toInt(), resolvedY)
  }
}
