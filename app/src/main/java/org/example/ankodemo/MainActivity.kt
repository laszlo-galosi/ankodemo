package org.example.ankodemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import org.example.ankodemo.ankoviews.inflatedAnkoView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.internals.AnkoInternals
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity(), AppbarUI.Callback {
    private val activityUI: MainActivityUI by lazy {
        MainActivityUI()
    }

    private var appbarView: View? = null

    private val loginFromUI: LoginFormUI by lazy {
        LoginFormUI(this)
    }

    private val scrollContent: NestedScrollView? by lazy {
        appbarView?.findViewById<NestedScrollView>(R.id.scrollable)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUI.setContentView(this)
        appbarView = AppbarUI(this, callback = this).view
        scrollContent?.let {
            AnkoInternals.addView(it, RichView(this))
        }
    }

    override fun onNavigationIconClicked(v: View, crossfade: Float) {
        if (crossfade >= 1.0f) {
            if (shouldGoback()) {
                onBackPressed()
            } else {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun AppCompatActivity.shouldGoback() = this.supportFragmentManager.backStackEntryCount > 1
}

class MainActivityUI : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        inflatedAnkoView(R.layout.activity_main)
    }
}


