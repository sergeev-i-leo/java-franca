package me.swift.android_test_application

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import me.swift.step_gs.Page

class AndroidDeviceView(
  context: Context,
  private val androidDevice: AndroidDevice,
  private val page: Page
) : View(context) {

  private val painter = AndroidPainter()

  private var activePointerId = -1
  private var lastX = 0f
  private var lastY = 0f

  private var scheduledExecutorService: ScheduledExecutorService? = null
  private var lastTickTime = 0L
  private val mainHandler = Handler(Looper.getMainLooper())
  private val isPainting = AtomicBoolean(false)

  init {
    isFocusable = true
    isFocusableInTouchMode = true
  }

  fun getPage(): Page {
    return page;
  }

  fun getDevice(): AndroidDevice = androidDevice

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
        page.handlePointerDown(x, y, 1)
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
    val startTime = androidDevice.time
    super.onDraw(canvas)

    isPainting.set(true)
    try {
      painter.setCanvas(canvas)
      page.paint(painter)
    } finally {
      isPainting.set(false)
    }

    val elapsedTime = androidDevice.getTime() - startTime
    if (elapsedTime > 12) {
      Log.w("AndroidDeviceView", "Slow frame: ${elapsedTime}ms")
    }
  }

  fun startRepainting() {
    if (scheduledExecutorService != null) {
      return
    }

    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    lastTickTime = androidDevice.time
    scheduledExecutorService?.scheduleAtFixedRate(
      { tick() },
      0,
      2,
      TimeUnit.MILLISECONDS
    )
  }

  private fun tick() {
    val tickTime = androidDevice.time
    if (tickTime - lastTickTime < 16) {
      return
    }

    lastTickTime = tickTime

    if ((!isPainting.get()) && (page.needsRepainting())) {
      mainHandler.post { invalidate() }
    }

    if (!page.needsNextRepainting()) {
      scheduledExecutorService?.shutdown()
      scheduledExecutorService = null
    }
  }
}