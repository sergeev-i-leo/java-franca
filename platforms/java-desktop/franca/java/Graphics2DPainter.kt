package franca.java

import franca.java.ui.Painter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints

class Graphics2DPainter(
  private val graphics2DDevice: Graphics2DDevice,
  private var graphics2D: Graphics2D
) : Painter() {

  init {
    graphics2D.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    )
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

    val font = graphics2DDevice.resolveFont(currentFontName, currentFontWeight, currentFontStyle, null)
    val metrics = graphics2D.getFontMetrics(font)
    return metrics.stringWidth(text)
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

    val font = graphics2DDevice.resolveFont(currentFontName, currentFontWeight, currentFontStyle, null)
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
    graphics2D.font = graphics2DDevice.resolveFont(currentFontName, currentFontWeight, currentFontStyle, scale)

    val withOpacity = ((color shr 24) and 0xFF) != 0
    val finalColor = if (withOpacity) {
      color
    } else {
      (currentOpacity shl 24) or (color and 0x00FFFFFF)
    }
    graphics2D.color = Color(finalColor, true)
    //graphics2D.drawString(text, (x * scale / PRECISION).toInt(), (y * scale / PRECISION).toInt())
    graphics2D.drawString(text, 0, 0)
  }
}
