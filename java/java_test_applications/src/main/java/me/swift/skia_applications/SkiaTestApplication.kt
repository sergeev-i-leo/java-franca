package me.swift.skia_applications

import org.jetbrains.skiko.*
import me.swift.engine.Page
import me.swift.engine.test_components.TestView0
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.SwingUtilities

class SkiaTestApplication {

  private val skiaLayer = SkiaLayer()
  private val skiaDevice = SkiaDevice(this)
  private val page = Page(skiaDevice)

  private var scheduler: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      SkiaTestApplication()
    }
  }

  init {
    page.views.add(TestView0())

    skiaLayer.skikoView = object : SkikoView {
      override fun onRender(canvas: org.jetbrains.skia.Canvas, width: Int, height: Int, nanoTime: Long) {
        canvas.clear(0xFFFFFFFF.toInt())
        page.paint(SkiaPainter(canvas))
      }
    }

    skiaLayer.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          page.handlePointerDown(mouseEvent.x.toFloat(), mouseEvent.y.toFloat(), 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          page.handlePointerDown(mouseEvent.x.toFloat(), mouseEvent.y.toFloat(), 3)
        }
      }
    })

    val frame = JFrame("SkiaTestApplication")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.add(skiaLayer)
    frame.setSize(800, 600)
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
  }

  fun startRepainting() {
    if (scheduler != null) {
      return
    }

    scheduler = Executors.newSingleThreadScheduledExecutor()
    lastTickTime = skiaDevice.time
    scheduler?.scheduleAtFixedRate({
      val tickTime = skiaDevice.time
      if (tickTime - lastTickTime < 16) {
        return@scheduleAtFixedRate
      }
      lastTickTime = tickTime

      if (page.needsRepainting()) {
        SwingUtilities.invokeLater { skiaLayer.needRedraw() }
      }

      if (!page.needsNextRepainting()) {
        scheduler?.shutdown()
        scheduler = null
      }
    }, 0, 2, TimeUnit.MILLISECONDS)
  }
}


