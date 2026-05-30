package me.swift.android_test_application

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.View
import me.swift.engine.Page

class AndroidDeviceView(
  context: Context
) : View(context) {

  private val device = AndroidDevice(this)

  private var page: Page? = null
  private val painter = AndroidPainter()

  private var activePointerId = -1
  private var lastX = 0f
  private var lastY = 0f

  init {
    isFocusable = true
    isFocusableInTouchMode = true
  }

  fun setPage(page: Page) {
    this.page = page
    invalidate()  // перерисовать сразу
  }

  fun getPage(): Page? {
    return page;
  }

  fun getDevice(): AndroidDevice = device

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val action = event.actionMasked
    val pointerIndex = event.actionIndex

    when (action) {
      MotionEvent.ACTION_DOWN -> {
        activePointerId = event.getPointerId(pointerIndex)
        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)
        lastX = x
        lastY = y
        page?.handlePointerDown(x, y, 1)
        return true
      }

      MotionEvent.ACTION_MOVE -> {
        val idx = event.findPointerIndex(activePointerId)
        if (idx < 0) return true

        val x = event.getX(idx)
        val y = event.getY(idx)
        val dx = x - lastX
        val dy = y - lastY

        if ((dx != 0f) || (dy != 0f)) {
          //page?.handlePointerMove(x, y, dx, dy, 1)
          lastX = x
          lastY = y
        }
        return true
      }

      MotionEvent.ACTION_UP -> {
        val idx = event.findPointerIndex(activePointerId)
        if (idx >= 0) {
          val x = event.getX(idx)
          val y = event.getY(idx)
          //page?.handlePointerUp(x, y, 1)
        }
        activePointerId = -1
        return true
      }

      MotionEvent.ACTION_CANCEL -> {
        //page?.handlePointerCancel(1)
        activePointerId = -1
        return true
      }
    }

    return super.onTouchEvent(event)
  }

  override fun onDraw(canvas: Canvas) {
    val startTime = device.getTime()
    super.onDraw(canvas)

    device.onPaintStart()
    try {
      painter.setCanvas(canvas)
      page?.paint(painter)
    } finally {
      device.onPaintEnd()
    }

    val elapsedTime = device.getTime() - startTime
    if (elapsedTime > 12) {
      Log.w("AndroidDeviceView", "Slow frame: ${elapsedTime}ms")
    }
  }
}