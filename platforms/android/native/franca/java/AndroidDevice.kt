package franca.java

import franca.java.graphics.device.Device

class AndroidDevice(
) : Device() {

  override fun getTime(): Long = System.currentTimeMillis()
}