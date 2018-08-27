@file:Suppress("UNCHECKED_CAST")

package org.example.ankodemo.ankoviews

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewManager
import android.widget.FrameLayout
import org.example.ankodemo.R
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView

/**
 * Created by László Gálosi on 04/12/17
 */
@SuppressLint("ViewConstructor")
open class InflatedAnkoView(context: Context, @LayoutRes var layoutRes: Int = 0,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.InflatedAnkoView)
        if (a.hasValue(R.styleable.InflatedAnkoView_layout)) {
            layoutRes = a.getInt(R.styleable.InflatedAnkoView_layout, 0)
        }
        a.recycle()
        init()
    }

    open fun init() = AnkoContext.createDelegate(this).apply {
        if (layoutRes > 0) {
            addView(LayoutInflater.from(context).inflate(layoutRes, this@InflatedAnkoView,
                    false))
        }
        bind()
    }

    fun bind() {}
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.inflatedAnkoView(@LayoutRes layoutRes: Int, theme: Int = 0,
        init: InflatedAnkoView.() -> Unit = {}) = ankoView(
        { InflatedAnkoView(it, layoutRes) }, theme, init)
