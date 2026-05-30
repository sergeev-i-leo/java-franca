package me.swift.android_test_application

class MainActivity : AppCompatActivity() {

  private lateinit var deviceView: AndroidDeviceView

  // Страницы
  private lateinit var page1: Page
  private lateinit var page2: Page
  private lateinit var page3: Page

  private var currentPageIndex = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    supportActionBar?.hide()
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    // Создаём View — она сама создаст Device
    deviceView = AndroidDeviceView(this)
    setContentView(deviceView)

    // Создаём страницы
    page1 = Page(deviceView.getDevice())
    page2 = Page(deviceView.getDevice())
    page3 = Page(deviceView.getDevice())

    setupPages()

    // Показываем первую страницу
    showPage(0)

    setupBottomNavigation()
  }

  private fun setupPages() {
    // Page 1
    page1.views.add(TestView0())

    // Page 2 - Hello World
    val helloView = object : View() {
      override fun paint(device: Device, painter: Painter, page: Page) {
        painter.setColor(0xFFFF0000.toInt())
        painter.setTextSize(50f)
        painter.drawText("Hello World!", 100f, 200f)
      }
    }
    page2.views.add(helloView)

    // Page 3 - копия
    page3.views.add(TestView0())
    val cloneLabel = object : View() {
      override fun paint(device: Device, painter: Painter, page: Page) {
        painter.setColor(0xFF00FF00.toInt())
        painter.setTextSize(24f)
        painter.drawText("Clone Page", 100f, 400f)
      }
    }
    page3.views.add(cloneLabel)
  }

  private fun showPage(index: Int) {
    currentPageIndex = index
    val page = when (index) {
      0 -> page1
      1 -> page2
      else -> page3
    }
    deviceView.setPage(page)
  }

  private fun setupBottomNavigation() {
    // BottomNavigationView можно добавить через layout или программно
    // ...
  }
}