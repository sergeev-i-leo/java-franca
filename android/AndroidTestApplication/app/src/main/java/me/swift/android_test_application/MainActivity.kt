package me.swift.android_test_application

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : Activity() {

  private lateinit var androidNavigator: AndroidNavigator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val bottomNav = BottomNavigationView(this).apply {
      inflateMenu(R.menu.bottom_nav_menu)
      itemIconTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.bottom_nav_color)
      itemTextColor = ContextCompat.getColorStateList(this@MainActivity, R.color.bottom_nav_color)
    }


    val container = FrameLayout(this).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        0,
        1f  // вес 1 — занимает всё свободное место
      )
    }

    val root = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      addView(container)
      addView(bottomNav)
    }

    setContentView(root)

    androidNavigator = AndroidNavigator(this, container, bottomNav)
  }
}