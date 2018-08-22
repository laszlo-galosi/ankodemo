@file:Suppress("UNCHECKED_CAST")

package org.example.ankodemo.ankoviews

import android.annotation.SuppressLint
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
@SuppressLint("ViewConstructor")
class InflatedDataBindingAnkoView(context: Context, @LayoutRes layoutRes: Int = 0) :
        InflatedAnkoView(context, layoutRes) {

    lateinit var viewBinding: ViewDataBinding

    override fun init() = AnkoContext.createDelegate(this).apply {
        if (layoutRes > 0) {
            viewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes,
                    this@InflatedDataBindingAnkoView, false)
            addView(viewBinding.root)
        }
        bind()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.inflatedDataBindingAnkoView(@LayoutRes layoutRes: Int,
        init: InflatedDataBindingAnkoView.() -> Unit,
        theme: Int = 0) = ankoView(
        { InflatedDataBindingAnkoView(it, layoutRes) }, theme, init)
