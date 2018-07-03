package org.example.ankodemo

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.view.View

/**
 * Created by László Gálosi on 04/12/17
 */

fun Int.resName(ctx: Context): String? = ctx.resources.getResourceEntryName(this)

fun View.visible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.visiblePlace(show: Boolean): Unit {
    this.visibility = if (show) View.VISIBLE else View.INVISIBLE
}

fun ViewDataBinding.find(@IdRes id: Int): View? = this.root.findViewById(id)

fun Context.colorRes(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)
