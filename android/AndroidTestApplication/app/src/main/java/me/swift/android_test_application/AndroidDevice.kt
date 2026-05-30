package me.swift.android_test_application

import android.os.Handler
import android.os.Looper
import me.swift.engine.Device
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class AndroidDevice(
  private val view: AndroidDeviceView
) : Device() {

  private var scheduledExecutorService: ScheduledExecutorService? = null
  private var lastTickTime = 0L
  private val mainHandler = Handler(Looper.getMainLooper())
  private val isPainting = AtomicBoolean(false)

  override fun getTime(): Long = System.currentTimeMillis()

  override fun startRepainting() {
    if (scheduledExecutorService != null) return

    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    lastTickTime = getTime()
    scheduledExecutorService?.scheduleAtFixedRate(
      { tick() },
      0,
      2,
      TimeUnit.MILLISECONDS
    )
  }

  private fun tick() {
    val tickTime = getTime()
    if (tickTime - lastTickTime < 16) return

    lastTickTime = tickTime

    val currentPage = view.getPage()

    if ((!isPainting.get()) && (currentPage?.needsRepainting() == true)) {
      mainHandler.post { view.invalidate() }
    }

    if (currentPage?.needsNextRepainting() != true) {
      scheduledExecutorService?.shutdown()
      scheduledExecutorService = null
    }
  }

  fun onPaintStart() {
    isPainting.set(true)
  }

  fun onPaintEnd() {
    isPainting.set(false)
  }
}