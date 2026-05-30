package me.swift.android_test_application

import android.graphics.Canvas
import android.graphics.Paint
import me.swift.engine.painter.Painter

class AndroidPainter(
) : Painter() {

  private var canvas: Canvas? = null
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  fun setCanvas(canvas: Canvas) {
    this.canvas = canvas;
  }

  override fun paintText(text: String, x: Float, y: Float, deviceFontKey: String?, deviceColor: Int) {
    paint.apply {
      color = 0xFFFF0000.toInt()
      isAntiAlias = true
      style = Paint.Style.FILL
      textSize = 40f
      letterSpacing = 0f
      isElegantTextHeight = false
    }

    canvas?.drawText(text, x, y, paint)
  }
}