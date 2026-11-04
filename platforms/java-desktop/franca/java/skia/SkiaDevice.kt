package franca.java.skia

import franca.java.Callback
import franca.java.ui.Device
import franca.java.ui.Painter
import org.jetbrains.skia.Data
import org.jetbrains.skia.Typeface
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

class SkiaDevice() : Device() {

  val skiaLayer = SkiaLayer()

  private var scheduler: ScheduledExecutorService? = null
  private var lastTickTime = 0L

  var primaryTypeface: Typeface? = null
  var secondaryTypeface: Typeface? = null
  var tertiaryTypeface: Typeface? = null
  var condensedTypeface: Typeface? = null

  var fontSize = 18 * Painter.PRECISION;

  init {

    val skiaDevice = this

    skiaLayer.skikoView = object : SkikoView {
      override fun onRender(canvas: org.jetbrains.skia.Canvas, width: Int, height: Int, nanoTime: Long) {
        val skiaPainter = SkiaPainter(skiaDevice, canvas);
        paint(skiaPainter)
        startPainting();
      }
    }

    skiaLayer.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
          handlePointerDown(mouseEvent.x, mouseEvent.y, 1)
        } else if (mouseEvent.button == MouseEvent.BUTTON3) {
          handlePointerDown(mouseEvent.x, mouseEvent.y, 3)
        }
      }
    })
  }

  override fun init(callback: Callback<Int?>) {
    primaryTypeface = loadFontFromResources("inter_regular.ttf") ?: Typeface.makeDefault()
    secondaryTypeface = loadFontFromResources("zero_cool_regular.ttf") ?: Typeface.makeDefault()
    tertiaryTypeface = loadFontFromResources("shantell_sans_bold.ttf") ?: Typeface.makeDefault()
    condensedTypeface = loadFontFromResources("fira_sans_extra_condensed.ttf") ?: Typeface.makeDefault()

    isReady = true

    callback.onResult(200)
  }

  private fun loadFontFromResources(resourcePath: String): Typeface? {
    return try {
      val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath) ?: return null

      val bytes = stream.readAllBytes()
      stream.close()

      return Typeface.makeFromData(Data.makeFromBytes(bytes))
    } catch (exception: Exception) {
      exception.printStackTrace()
      null
    }
  }

  override fun getTime(): Long {
    return System.nanoTime() / 1000000
  }

  override fun getWidth(): Float {
    return skiaLayer.width * Painter.PRECISION;
  }

  override fun getHeight(): Float {
    return skiaLayer.height * Painter.PRECISION;
  }

  override fun startPainting() {
    if (scheduler != null) {
      return
    }

    scheduler = Executors.newSingleThreadScheduledExecutor()
    scheduler?.scheduleAtFixedRate({
      val tickTime = time
      if (tickTime - lastTickTime < 16) {
        return@scheduleAtFixedRate
      }
      lastTickTime = tickTime

      if (needsPainting()) {
        SwingUtilities.invokeLater { skiaLayer.needRedraw() }
      }

      if (!needsNextPainting()) {
        scheduler?.shutdown()
        scheduler = null
      }
    }, 0, 2, TimeUnit.MILLISECONDS)
  }
}
