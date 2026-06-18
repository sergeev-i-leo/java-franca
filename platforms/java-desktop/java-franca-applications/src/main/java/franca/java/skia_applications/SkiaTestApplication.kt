package franca.java.skia_applications

import franca.java.graphics.Page
import franca.java.test_components.TestView0
import franca.java.skia.SkiaApplication
import franca.java.skia.SkiaRouter

class SkiaTestApplication {

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val skiaRouter = SkiaRouter()
      val page = Page(skiaRouter)
      page.views.add(TestView0())

      skiaRouter.pushPage(page);

      SkiaApplication(skiaRouter);
    }
  }
}

