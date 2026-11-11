package franca.java

import org.jetbrains.skia.Font
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Typeface
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SkiaFont(
  typeface: Typeface,
  fontSize: Float
) {

  val font = Font(typeface, fontSize)

  var ascent = 0.0f
  var descent = 0.0f
  var leading = 0.0f
  var capHeight = 0.0f
  var xHeight = 0.0f
  var top = 0.0f
  var bottom = 0.0f
  var lineHeight = 0.0f
  var height = 0
  var baselineY = 0.0f

  init {
    val metrics = font.metrics
    ascent = metrics.ascent
    descent = metrics.descent
    leading = metrics.leading
    capHeight = metrics.capHeight
    xHeight = metrics.xHeight
    top = metrics.top
    bottom = metrics.bottom
    lineHeight = descent - ascent + leading
    height = (descent - ascent).roundToInt()
    baselineY = computeBaselineY(font)
  }

  private fun computeBaselineY(font: Font): Float {
    val text = "I"

    val glyphs = font.getStringGlyphs(text)
    val rects = font.getBounds(glyphs)
    if (rects.isEmpty()) {
      return 0f
    }

    var left = Float.POSITIVE_INFINITY
    var top = Float.POSITIVE_INFINITY
    var right = Float.NEGATIVE_INFINITY
    var bottom = Float.NEGATIVE_INFINITY

    for (rect in rects) {
      left = min(rect.left, left)
      top = min(rect.top, top)
      right = max(rect.right, right)
      bottom = max(rect.bottom, bottom)
    }

    val bounds = Rect.makeLTRB(left, top, right, bottom)

    return bounds.height / 2f - bounds.bottom
  }

  fun measureTextWidth(text: String): Int = font.measureTextWidth(text).roundToInt()
}
