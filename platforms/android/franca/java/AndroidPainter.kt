package franca.java

import android.graphics.Canvas
import android.graphics.Paint
import franca.java.ui.Painter

class AndroidPainter(
) : Painter() {

  private var canvas: Canvas? = null
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  fun setCanvas(canvas: Canvas) {
    this.canvas = canvas;
  }

  override fun paintText(text: String, x: Int, y: Int, painterFontName: String, deviceColor: Int, scale: Float) {
    paint.apply {
      color = 0xFFFF0000.toInt()
      isAntiAlias = true
      style = Paint.Style.FILL
      textSize = 40f
      letterSpacing = 0f
      isElegantTextHeight = false
    }

    canvas?.drawText(text, x.toFloat() * scale / Painter.MULTIPLIER.toFloat(), y.toFloat() * scale / Painter.MULTIPLIER.toFloat(), paint)
  }
}