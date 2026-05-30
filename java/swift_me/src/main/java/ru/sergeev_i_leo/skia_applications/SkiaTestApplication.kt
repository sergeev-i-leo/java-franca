package ru.sergeev_i_leo.skia_applications

import org.jetbrains.skiko.*
import ru.sergeev_i_leo.engine.Page
import ru.sergeev_i_leo.engine.test_components.TestView0
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame

class SkiaTestApplication {

  private val skiaLayer = SkiaLayer()
  private val skiaDevice = SkiaDevice(skiaLayer)
  private val page = Page(skiaDevice)

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      SkiaTestApplication()
    }
  }

  init {
    page.views.add(TestView0())

    skiaDevice.setPage(page)

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
}


