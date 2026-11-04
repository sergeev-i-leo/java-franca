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

  override fun fillCircle(x: Float, y: Float, radius: Float, deviceColor: Int, scale: Float) {
    paint.apply {
      color = deviceColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

    val px = x * scale / Painter.PRECISION
    val py = y * scale / Painter.PRECISION
    val pr = radius * scale / Painter.PRECISION

    canvas?.drawCircle(px, py, pr, paint)
  }

  override fun paintText(text: String, x: Float, y: Float, painterFontName: String, deviceColor: Int, scale: Float) {
    paint.apply {
      color = 0xFFFF0000.toInt()
      isAntiAlias = true
      style = Paint.Style.FILL
      textSize = 40f
      letterSpacing = 0f
      isElegantTextHeight = false
    }

    canvas?.drawText(text, x * scale / Painter.PRECISION, y * scale / Painter.PRECISION, paint)
  }
}
