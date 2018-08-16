@file:Suppress("UNCHECKED_CAST")

package org.example.ankodemo.ankoviews

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewManager
import android.widget.FrameLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView

/**
 * Created by László Gálosi on 04/12/17
 */
open class InflatedAnkoView : FrameLayout {

    open fun init(@LayoutRes layoutRes: Int) = AnkoContext.createDelegate(this).apply {
        if (layoutRes > 0) {
            addView(LayoutInflater.from(context).inflate(layoutRes, this@InflatedAnkoView, false))
        }
        bind()
    }

    fun bind() {}

    constructor(context: Context) : super(context) {
        init(0)
    }

    constructor(context: Context, @LayoutRes layoutRes: Int) : super(context) {
        init(layoutRes)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.inflatedAnkoView(@LayoutRes layoutRes: Int,
        init: InflatedAnkoView.() -> Unit,
        theme: Int = 0) = ankoView(
        { InflatedAnkoView(it, layoutRes) }, theme, { init(); bind() })
