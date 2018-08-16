@file:Suppress("UNCHECKED_CAST")

package org.example.ankodemo.ankoviews

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewManager
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView

/**
 * Created by László Gálosi on 04/12/17
 */
class InflatedDataBindingAnkoView : InflatedAnkoView {

    lateinit var viewBinding: ViewDataBinding

    override fun init(@LayoutRes layoutRes: Int) = AnkoContext.createDelegate(this).apply {
        if (layoutRes > 0) {
            viewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes,
                    this@InflatedDataBindingAnkoView, false)
            viewBinding.executePendingBindings()
            addView(viewBinding.root)
        }
        super.bind()
    }

    constructor(context: Context) : super(context) {
        init(0)
    }

    constructor(context: Context, @LayoutRes layoutRes: Int) : super(context) {
        init(layoutRes)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.inflatedDataBindingAnkoView(@LayoutRes layoutRes: Int,
        init: InflatedDataBindingAnkoView.() -> Unit,
        theme: Int = 0) = ankoView(
        { InflatedDataBindingAnkoView(it, layoutRes) }, theme, { init(); bind() })
