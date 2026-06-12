package franca.java.platforms.skia

import franca.java.graphics.device.Router

class SkiaRouter : Router() {

  var skiaApplication: SkiaApplication? = null

  override fun startRepainting() {
    skiaApplication?.startRepainting();
  }
}
