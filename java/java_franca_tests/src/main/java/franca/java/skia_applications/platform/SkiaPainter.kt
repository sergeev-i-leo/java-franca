package franca.java.skia_applications.platform

import franca.java.graphics.device.Painter

class SkiaPainter(private val canvas: org.jetbrains.skia.Canvas) : Painter() {

  private val textPaint = org.jetbrains.skia.Paint().apply {
    color = 0xFF000000.toInt()
    isAntiAlias = true
  }

  private val textFont = org.jetbrains.skia.Font(
    org.jetbrains.skia.Typeface.makeFromName("Arial", org.jetbrains.skia.FontStyle.BOLD),
    48f
  )

  override fun paintText(text: String, x: Float, y: Float, deviceFontKey: String, deviceColor: Int) {
    canvas.drawString(text, x, y, textFont, textPaint)
  }
}
