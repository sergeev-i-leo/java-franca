package franca.java.skia_applications

import franca.java.SpinnerView
import franca.java.TestView0
import franca.java.skia.SkiaRouter
import franca.java.ui.Page
import javax.swing.JFrame

class SkiaTestApplication {

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val skiaRouter = SkiaRouter()
      val page = Page(skiaRouter)
      page.views.add(TestView0())
      page.views.add(SpinnerView(6000f))

      skiaRouter.pushPage(page);

      val frame = JFrame("SkiaTestApplication")
      frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
      frame.add(skiaRouter.skiaLayer)
      frame.setSize(800, 600)
      frame.setLocationRelativeTo(null)
      frame.isVisible = true

    }
  }
}

