package franca.java.skia_applications.platform

import franca.java.graphics.device.Router

class SkiaRouter : Router() {

  var skiaApplication: SkiaApplication? = null

  override fun startRepainting() {
    skiaApplication?.startRepainting();
  }
}
