package org.example.ankodemo

import android.os.Bundle
import android.support.v4.app.TaskStackBuilder
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
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.internals.AnkoInternals
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.toast
import trikita.log.Log

class LoginActivity : AppCompatActivity() {
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

    val scrollView: NestedScrollView? by lazy {
        findViewById<NestedScrollView>(R.id.scrollable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivityUI = LoginActivityUI()
        mainActivityUI.setContentView(this)
        val ctx = AnkoContext.create(LoginActivity@ this, LoginActivity@ this)
        val loginFormUI = ctx.loginForm {
            cbOnValidated = { user, passw -> tryLogin(ctx, user, passw) }
        }.applyRecursively(customStyle)
        scrollView?.let {
            AnkoInternals.addView(it, loginFormUI)
        }

    }

    fun tryLogin(ui: AnkoContext<LoginActivity>, email: CharSequence?, password: CharSequence?) {
        ui.doAsync {
            Thread.sleep(500)

            activityUiThreadWithContext {
                Log.d("tryLogin", email, password)
                if (checkCredentials(email.toString(), password.toString())) {
                    toast("Logged in! :)")
//                    val startIntent = intentFor<MainActivity>()
//                    startIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                    /*val startIntent: Intent? =*/ TaskStackBuilder.create(this)
                            // add all of MainActivity's parent to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(intentFor<MainActivity>())
                            .startActivities()

//                    startActivity(startIntent)
                } else {
                    toast("Wrong password :( Enter:password")
                }
            }
        }
    }

    private fun checkCredentials(email: String?, password: String?) =
            email == "laszlo.galosi@gmail.com" && password == "asdasdasd"
}

class LoginActivityUI : AnkoComponent<LoginActivity> {
    override fun createView(ui: AnkoContext<LoginActivity>) = with(ui) {
        inflatedAnkoView(R.layout.activity_login)
    }
}
