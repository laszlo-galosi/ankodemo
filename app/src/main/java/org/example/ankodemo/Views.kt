package org.example.ankodemo

import android.content.Context
import android.databinding.ViewDataBinding
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.Spanned
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.childrenRecursiveSequence

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

fun String.toHtml(): Spanned = Html.fromHtml(this)

fun Context.resName(resource: Int): String? = this.resources.getResourceEntryName(resource)

fun View.resName(): String? = this.resources.getResourceEntryName(this.id)

fun ViewGroup.touchedViewsFor(event: MotionEvent?): Sequence<View> {
    if (event == null) return emptySequence()
    val root = this
    return root.childrenRecursiveSequence()
            .filter { v ->
                event.x >= v.left && event.x <= v.right &&
                        event.y >= v.top && event.y <= v.bottom
            }
}

private fun <T : View> View.findParent(clazz: Class<T>): T? {
    if (this.parent == null) return null
    return if (this.parent.javaClass == clazz) this.parent as T
    else (this.parent as View).findParent(clazz)
}

fun Context.toDrawable(@DrawableRes resource: Int): Drawable? = ContextCompat.getDrawable(this,
        resource)
