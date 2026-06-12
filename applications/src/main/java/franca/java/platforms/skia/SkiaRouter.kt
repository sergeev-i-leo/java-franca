package franca.java.platforms.skia

import java.franca.graphics.device.Router

class SkiaRouter : Router() {

  var skiaApplication: SkiaApplication? = null

  override fun startRepainting() {
    skiaApplication?.startRepainting();
  }
}
