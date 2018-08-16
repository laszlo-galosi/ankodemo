package org.example.ankodemo.ankoviews

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.internals.AnkoInternals

/**
 * @author László Gálosi
 * @since 15/08/18
 */
interface AnkoViewProvider {
    val view: View
}

inline fun <T> ViewManager.ankoComponent(factory: (ctx: Context) -> T, theme: Int,
        init: T.() -> Unit): View
        where T : AnkoComponent<ViewGroup>, T : AnkoViewProvider {
    val ctx = AnkoInternals.wrapContextIfNeeded(AnkoInternals.getContext(this), theme)
    val ui = factory(ctx)
    ui.init()
    AnkoInternals.addView(this, ui.view)
    return ui.view
}
