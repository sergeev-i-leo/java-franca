package franca.java.skia_applications

import franca.java.Router0
import franca.java.SkiaDevice
import javax.swing.JFrame

class SkiaTestApplication {

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val skiaDevice = SkiaDevice()

      skiaDevice.router = Router0(skiaDevice)

      val frame = JFrame("SkiaTestApplication")
      frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
      frame.add(skiaDevice.skiaLayer)
      frame.setSize(800, 600)
      frame.setLocationRelativeTo(null)
      frame.isVisible = true

    }
  }
}

