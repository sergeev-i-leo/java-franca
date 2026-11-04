package franca.java.ws

import franca.java.ui.Device
import franca.java.graphics.GraphicsDevice
import franca.java.skia.SkiaDevice

class WsDevice(var graphicsRouter: GraphicsDevice, var skiaRouter: SkiaDevice) : Device() {
  var wsServer: WsServer? = null
}
