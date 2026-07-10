package franca.java.skia

import franca.java.graphics.device.Router
import franca.java.skia.SkiaApplication

class SkiaRouter : Router() {

  var skiaApplication: SkiaApplication? = null

  override fun startRepainting() {
    skiaApplication?.startRepainting();
  }
}
