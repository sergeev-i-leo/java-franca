package franca.java.skia

import franca.java.common.JavaDevice
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.SwingUtilities

class SkiaApplication(private val skiaRouter: SkiaRouter) {

  private val skiaLayer = SkiaLayer()
  private val javaDevice = JavaDevice()

  private var scheduler: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  init {
    skiaRouter.skiaApplication = this;
    skiaRouter.device = javaDevice;

    skiaLayer.skikoView = object : SkikoView {
      override fun onRender(canvas: org.jetbrains.skia.Canvas, width: Int, height: Int, nanoTime: Long) {
        canvas.clear(0xFFFFFFFF.toInt())
        skiaRouter.paint(SkiaPainter(canvas))
      }
    }

    skiaLayer.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          skiaRouter.handlePointerDown(mouseEvent.x.toFloat(), mouseEvent.y.toFloat(), 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          skiaRouter.handlePointerDown(mouseEvent.x.toFloat(), mouseEvent.y.toFloat(), 3)
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
    lastTickTime = javaDevice.time
    scheduler?.scheduleAtFixedRate({
      val tickTime = javaDevice.time
      if (tickTime - lastTickTime < 16) {
        return@scheduleAtFixedRate
      }
      lastTickTime = tickTime

      if (skiaRouter.needsRepainting()) {
        SwingUtilities.invokeLater { skiaLayer.needRedraw() }
      }

      if (!skiaRouter.needsNextRepainting()) {
        scheduler?.shutdown()
        scheduler = null
      }
    }, 0, 2, TimeUnit.MILLISECONDS)
  }
}
