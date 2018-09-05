package org.example.ankodemo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import org.example.ankodemo.ankoviews.inflatedAnkoView
import org.example.ankodemo.util.DrawerContent
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.internals.AnkoInternals
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.setContentView
import trikita.log.Log

class MainActivity : AppCompatActivity(), AppbarUI.Callback {
    private val activityUI: MainActivityUI by lazy {
        MainActivityUI()
    }

    private var appbarView: View? = null

    private val scrollContent: NestedScrollView? by lazy {
        appbarView?.find<NestedScrollView>(R.id.scrollable)
    }

    private val drawerMenu: DrawerContent by lazy {
        findViewById<DrawerContent>(R.id.menu)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUI.setContentView(this)
        appbarView = AppbarUI(this, callback = this).view
        scrollContent?.let {
            AnkoInternals.addView(it, RichView(this))
        }
        drawerMenu.childrenRecursiveSequence()
                .filter { v -> v is TextView }
                .forEach { v -> v.onClick { onMenuItemClicked(v) } }
    }

    private fun onMenuItemClicked(view: View) {
        when (view.id) {
            R.id.action_tutorial -> {
                val startIntent = intentFor<TutorialActivity>(
                        TutorialActivity.ARG_PARENT to this@MainActivity.javaClass.simpleName)
                startIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(startIntent)
            }
            else -> Log.d("onMenuItemClicked", view.javaClass.simpleName, view.id)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onNavigationIconClicked(v: View, crossfade: Float) {
        if (crossfade >= 1.0f) {
            if (shouldGoback()) {
                onBackPressed()
            } else {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerMenu.transitionToStart()
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


