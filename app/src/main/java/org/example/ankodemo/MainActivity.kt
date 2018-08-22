package org.example.ankodemo

import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import org.example.ankodemo.ankoviews.inflatedAnkoView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.activityUiThreadWithContext
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.internals.AnkoInternals
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import trikita.log.Log

class MainActivity : AppCompatActivity() {
    val customStyle = { v: Any ->
        when (v) {
            is Button -> {
                v.textSize = 26f
                v.backgroundColorResource = R.color.colorAccent
            }
//            is TextView -> v.textColorResource = android.R.color.white
            is EditText -> v.textSize = 20f
            is ViewGroup -> v.backgroundResource = R.color.colorPrimary
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivityUI = MainActivityUI()
        mainActivityUI.setContentView(this)
        val ctx = AnkoContext.create(MainActivity@ this, MainActivity@ this)
        val loginFormUI = ctx.loginForm {
            cbOnValidated = { user, passw -> tryLogin(ctx, user, passw) }
        }.applyRecursively(customStyle)
        AnkoInternals.addView(findViewById<NestedScrollView>(R.id.scroll_view), loginFormUI)

    }

    fun tryLogin(ui: AnkoContext<MainActivity>, name: CharSequence?, password: CharSequence?) {
        ui.doAsync {
            Thread.sleep(500)

            activityUiThreadWithContext {
                Log.d("tryLogin", name, password)
                if (checkCredentials(name.toString(), password.toString())) {
                    toast("Logged in! :)")
                    startActivity<CountriesActivity>()
                } else {
                    toast("Wrong password :( Enter:password")
                }
            }
        }
    }

    private fun checkCredentials(name: String,
            password: String) = name == "user" && password == "password"
}

class MainActivityUI : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        inflatedAnkoView(R.layout.activity_main)
    }
}
