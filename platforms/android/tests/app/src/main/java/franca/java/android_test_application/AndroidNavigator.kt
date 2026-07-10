package franca.java.android_test_application

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import franca.java.AndroidRouter
import franca.java.AndroidRouterView
import franca.java.TestView0
import franca.java.graphics.Page

class AndroidNavigator(
  private val context: Context,
  private val container: ViewGroup,
  private val bottomNavigationView: BottomNavigationView
) {

  private val homeAndroidRouter = AndroidRouter(this)
  private val homePage = Page(homeAndroidRouter)
  private val cloneAndroidRouter = AndroidRouter(this)
  private val clonePage = Page(cloneAndroidRouter)

  private val viewHome = AndroidRouterView(context, homeAndroidRouter)
  private val viewHello = createHelloWorldView()
  private val viewClone = AndroidRouterView(context, cloneAndroidRouter)

  var currentView: View = viewHome
    private set

  init {
    homeAndroidRouter.pushPage(homePage)
    homePage.views.add(TestView0())

    cloneAndroidRouter.pushPage(clonePage)
    clonePage.views.add(TestView0())

    listOf(viewHome, viewHello, viewClone).forEach { view ->
      view.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
    }

    container.addView(viewHome)
    container.addView(viewHello)
    container.addView(viewClone)

    showPage(0)

    setupBottomNavigation()
  }

  private fun createHelloWorldView(): View {
    return FrameLayout(context).apply {
      val textView = TextView(context).apply {
        text = "Hello World!"
        textSize = 40f
        setTextColor(Color.RED)
      }

      addView(textView)

      // Центрируем TextView при изменении размера
      addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
        val width = right - left
        val height = bottom - top

        textView.measure(
          View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.UNSPECIFIED),
          View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED)
        )

        textView.layout(
          (width - textView.measuredWidth) / 2,
          (height - textView.measuredHeight) / 2,
          (width + textView.measuredWidth) / 2,
          (height + textView.measuredHeight) / 2
        )
      }
    }
  }

  private fun setupBottomNavigation() {
    bottomNavigationView.setOnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.nav_home -> {
          showPage(0)
          true
        }
        R.id.nav_hello -> {
          showPage(1)
          true
        }
        R.id.nav_clone -> {
          showPage(2)
          true
        }
        else -> false
      }
    }
  }

  private fun showPage(index: Int) {
    viewHome.visibility = View.GONE
    viewHello.visibility = View.GONE
    viewClone.visibility = View.GONE

    currentView = when (index) {
      0 -> viewHome
      1 -> viewHello
      else -> viewClone
    }
    currentView.visibility = View.VISIBLE

    currentView.invalidate()
  }
}
