package franca.java

import java.awt.*
import javax.swing.JPanel

import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class SkiaPanel(
  val skiaDevice: SkiaDevice
) : JPanel() {

  val skiaLayer = SkiaLayer()

  init {
    layout = BorderLayout()
    background = Color.WHITE

    add(skiaLayer)

    skiaLayer.skikoView = object : SkikoView {
      override fun onRender(canvas: org.jetbrains.skia.Canvas, width: Int, height: Int, nanoTime: Long) {
        val skiaPainter = SkiaPainter(skiaDevice)
        skiaPainter.assignCanvas(canvas)
        skiaDevice.paint(skiaPainter)
        skiaDevice.startPainting();
      }
    }

    skiaLayer.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          skiaDevice.handlePointerDown(mouseEvent.x, mouseEvent.y, 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          skiaDevice.handlePointerDown(mouseEvent.x, mouseEvent.y, 3)
        }
      }
    })
  }
}
