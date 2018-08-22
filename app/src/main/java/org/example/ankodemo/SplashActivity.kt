package org.example.ankodemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.setContentView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplashActivityUI().setContentView(this)
        startActivity(intentFor<TutorialActivity>())
        overridePendingTransition(0, 0)
        finish()
    }
}

class SplashActivityUI : AnkoComponent<SplashActivity> {
    override fun createView(ui: AnkoContext<SplashActivity>) = with(ui) {
        frameLayout {
            backgroundResource = R.drawable.splash
            lparams(width = matchParent, height = matchParent)
        }
    }
}
