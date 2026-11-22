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

  override fun fillCircle(x: Int, y: Int, radius: Int, deviceColor: Int) {
    paint.apply {
      color = deviceColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

    val px = x.toFloat() / PRECISION.toFloat()
    val py = y.toFloat() / PRECISION.toFloat()
    val pr = radius.toFloat() / PRECISION.toFloat()

    canvas?.drawCircle(px, py, pr, paint)
  }

  override fun paintText(text: String, x: Int, y: Int, fontName: String?, fontWeight: String?, fontStyle: String?, color: Int, scale: Float) {
    paint.apply {
      isAntiAlias = true
      style = Paint.Style.FILL
      textSize = 40f
      letterSpacing = 0f
      isElegantTextHeight = false
    }

    canvas?.drawText(text, x * scale / Painter.PRECISION, y * scale / Painter.PRECISION, paint)
  }
}
