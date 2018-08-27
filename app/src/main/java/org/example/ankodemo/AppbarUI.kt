package org.example.ankodemo

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.constraint.utils.ImageFilterView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ViewStubCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import org.example.ankodemo.ankoviews.AnkoViewProvider
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext

@SuppressLint("ViewConstructor", "RestrictedApi")
class AppbarUI(context: Context,
        @LayoutRes val layoutRes: Int = R.layout.widget_scrolling_w_appbar,
        @IdRes val inflatedIdRes: Int = R.id.content,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        val title: String = context.getString(R.string.app_name),
        val backgroundDrawable: Drawable? = context.toDrawable(R.drawable.bringas_header),
        val logoDrawable: Drawable? = context.toDrawable(R.drawable.hello_logo),
        val navIconDrawable: Drawable? = context.toDrawable(R.drawable.ic_menu_24dp),
        val callback: Callback? = null)
    : View(context, attrs, defStyleAttr), AnkoComponent<View>, AnkoViewProvider,
        ViewStubCompat.OnInflateListener {

    private val viewStub: ViewStubCompat? by lazy {
        (context as AppCompatActivity).findViewById<ViewStubCompat>(inflatedIdRes)
    }

    override val view: View by lazy {
        createView(AnkoContext.create(context, this as View))
    }

    var viewBinding: ViewDataBinding? = null

    override fun createView(ui: AnkoContext<View>): View = with(ui) {
        viewStub?.let {
            it.layoutResource = layoutRes
            it.inflatedId = inflatedIdRes
            it.setOnInflateListener(this@AppbarUI)
        }
        viewStub?.inflate()!!
    }

    override fun onInflate(stub: ViewStubCompat, inflated: View) {
        viewBinding = DataBindingUtil.bind<ViewDataBinding>(inflated)
        viewBinding?.let { binding ->
            with(binding) {
                setVariable(BR.title, title)
                setVariable(BR.background, backgroundDrawable)
                setVariable(BR.logo, logoDrawable)
                setVariable(BR.navIcon, navIconDrawable)
                executePendingBindings()
                root.findViewById<ImageView>(R.id.logo).setOnClickListener { v ->
                    var crossfade = 1.0f
                    if (v is ImageFilterView) {
                        crossfade = v.crossfade
                    }
                    if (callback != null) {
                        callback.onNavigationIconClicked(v, crossfade)
                    }
                }
            }
        }
    }

    fun setNavigationDrawabke(navDrawable: Drawable?) {
        viewBinding?.let { binding ->
            with(binding) {
                setVariable(BR.navIcon, navDrawable)
                executePendingBindings()
            }
        }
    }

    interface Callback {
        /**
         * Called when the navigation icon is clicked.
         * @param v - the view of the click event
         * @param crossfade - the curren crossfade value if the navigation icon is
         * a subclass of ImageFilterView
         */
        fun onNavigationIconClicked(v: View, crossfade: Float)
    }

}
