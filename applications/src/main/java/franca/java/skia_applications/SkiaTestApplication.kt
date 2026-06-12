package franca.java.skia_applications

import java.franca.graphics.views.Page
import java.franca.graphics.test_components.TestView0
import franca.java.platforms.skia.SkiaApplication
import franca.java.platforms.skia.SkiaRouter

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

