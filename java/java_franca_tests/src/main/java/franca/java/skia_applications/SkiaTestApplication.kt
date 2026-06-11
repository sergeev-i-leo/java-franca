package franca.java.skia_applications

import franca.java.graphics.views.Page
import franca.java.graphics.test_components.TestView0
import franca.java.skia_applications.platform.SkiaApplication
import franca.java.skia_applications.platform.SkiaRouter

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

