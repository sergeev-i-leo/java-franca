package franca.java

import java.awt.Font
import java.awt.font.FontRenderContext
import kotlin.math.roundToInt

class Graphics2DFont(
  font: Font,
  fontSize: Float
) {

  val font: Font

  var ascent = 0.0f
  var descent = 0.0f
  var leading = 0.0f
  var height = 0
  var lineHeight = 0.0f
  var baselineY = 0.0f

  var derivedFont: Font
  var derivedSize: Float

  init {
    this.font = font.deriveFont(fontSize)
    derivedFont = this.font
    derivedSize = fontSize

    val fontRenderContext = FontRenderContext(null, true, true)
    val metrics = this.font.getLineMetrics("Ag", fontRenderContext)

    ascent = metrics.ascent
    descent = metrics.descent
    leading = metrics.leading
    height = (ascent + descent).roundToInt()
    lineHeight = ascent + descent + leading

    baselineY = (ascent - descent) / 2f + ascent * 0.05f
  }

  public fun deriveFont(size: Float): Font {
    if ((size * 100f).toInt() == (derivedSize * 100f).toInt()) {
      return derivedFont
    }
    derivedSize = size
    derivedFont = font.deriveFont(derivedSize)
    return derivedFont
  }
}
