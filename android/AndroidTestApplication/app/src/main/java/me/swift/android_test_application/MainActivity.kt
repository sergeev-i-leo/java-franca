package me.swift.android_test_application

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import me.swift.engine.Page

class MainActivity : AppCompatActivity() {

  lateinit var device: AndroidDevice
  lateinit var page: Page
  private lateinit var customView: CustomView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    customView = CustomView(this)
    setContentView(customView)

    device = AndroidDevice(this, customView)
    page = Page(device)

    customView.setPage(page)
  }

  inner class CustomView(context: Context) : View(context) {

    private var page: Page? = null
    private val painter = AndroidPainter()

    fun setPage(page: Page) {
      this.page = page
    }

    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)

      painter.setCanvas(canvas)
      page?.paint(painter)
    }
  }
}