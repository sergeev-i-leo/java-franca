package franca.java.skia

import franca.java.common.JavaDesktopRouter
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

class SkiaRouter() : JavaDesktopRouter() {

  val skiaLayer = SkiaLayer()

  private var scheduler: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  init {

    skiaLayer.skikoView = object : SkikoView {
      override fun onRender(canvas: org.jetbrains.skia.Canvas, width: Int, height: Int, nanoTime: Long) {
        canvas.clear(0xFFFFFFFF.toInt())
        paint(SkiaPainter(canvas))
      }
    }

    skiaLayer.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          handlePointerDown(mouseEvent.x.toFloat(), mouseEvent.y.toFloat(), 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          handlePointerDown(mouseEvent.x.toFloat(), mouseEvent.y.toFloat(), 3)
        }
      }
    })
  }

  override fun startRepainting() {
    if (scheduler != null) {
      return
    }

    scheduler = Executors.newSingleThreadScheduledExecutor()
    lastTickTime = time
    scheduler?.scheduleAtFixedRate({
      val tickTime = time
      if (tickTime - lastTickTime < 16) {
        return@scheduleAtFixedRate
      }
      lastTickTime = tickTime

      if (needsRepainting()) {
        SwingUtilities.invokeLater { skiaLayer.needRedraw() }
      }

      if (!needsNextRepainting()) {
        scheduler?.shutdown()
        scheduler = null
      }
    }, 0, 2, TimeUnit.MILLISECONDS)
  }
}
