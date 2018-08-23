package org.example.ankodemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.constraint.motion.MotionLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_tutorial.btn_close
import kotlinx.android.synthetic.main.activity_tutorial.motionLayout
import kotlinx.android.synthetic.main.activity_tutorial.viewPager
import kotlinx.android.synthetic.main.widget_bike.bikeLayout
import kotlinx.android.synthetic.main.widget_lets_start.button_content
import org.example.ankodemo.TutorialActivity.Page
import org.example.ankodemo.ankoviews.AnkoViewProvider
import org.example.ankodemo.ankoviews.inflatedDataBindingAnkoView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.setContentView
import trikita.log.Log
import java.util.ArrayList

class TutorialActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    /**
     * Iterate through this activity content's children views recursively to
     * determine the views touched by the given MotionEvent, and prints the
     * view id to the logcat.
     */
    @SuppressLint("NewApi")
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            (activityUI.viewBinding.root as ViewGroup)
                    .touchedViewsFor(event)
                    .filter { v -> v.id > 0 && event.action == MotionEvent.ACTION_UP }
                    .forEach { v ->
                        Log.d("onTouched", this.resName(v.id), v.javaClass.simpleName,
                                MotionEvent.actionToString(event.action), event.x, event.y)
                        Log.v(event)
                    }
        }
        return super.dispatchTouchEvent(event)
    }

    data class Page(val num: Int, @DrawableRes val iconRes: Int, @StringRes val titleRes: Int,
            @StringRes val detailsRes: Int, @IdRes val id: Int = R.id.tut_page)

    val firstPage = Page(1, R.drawable.ic_nav_tracking, R.string.page_start_stop,
            R.string.details_tut01, R.id.tut_page_1)

    val pages = listOf(
            firstPage,
            Page(2, R.drawable.ic_nav_coupons, R.string.page_coupons,
                    R.string.details_tut02, R.id.tut_page_2),
            Page(3, R.drawable.ic_nav_insurance, R.string.page_insurance,
                    R.string.details_tut03, R.id.tut_page_3),
            Page(4, R.drawable.ic_nav_service, R.string.page_service,
                    R.string.details_tut04, R.id.tut_page_4),
            Page(5, R.drawable.ic_nav_service, R.string.page_door_to_door,
                    R.string.details_tut05, R.id.tut_page_5),
            Page(6, R.drawable.ic_nav_bikephoto, R.string.page_bikephoto,
                    R.string.details_tut06, R.id.tut_page_6),
            Page(7, R.drawable.ic_nav_profile, R.string.page_profile,
                    R.string.details_tut07, R.id.tut_page_7)
    )

    private val activityUI by lazy {
        TutorialActivityUI()
    }

    private val pageAdapter: TutorialAdapter by lazy {
        val tutorialAdapter = TutorialAdapter(pageViews)
        tutorialAdapter
    }

    private val pageViews: MutableList<View> by lazy {
        val pageList = ArrayList<View>()
        pages.forEach { p ->
            val pageView = TutorialPageUI(p, this).view
            pageView.findViewById<MotionLayout>(R.id.motionLayout)?.let {
                motionLayout.setShowPaths(true)
            }
            pageList += pageView
        }
        pageList
    }

    private val startButton: AppCompatButton by lazy {
        motionLayout.findViewById<AppCompatButton>(R.id.lets_start)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activityUI.setContentView(this)
        viewPager.adapter = pageAdapter
        viewPager.currentItem = 0
        viewPager.addOnPageChangeListener(this)
        viewPager.addOnPageChangeListener(bikeLayout)
        val startAction = {
            startActivity(intentFor<MainActivity>())
            finish()
        }
        startButton.visibility = View.INVISIBLE
        startButton.onClick {
            startAction.invoke()
        }
        btn_close.onClick {
            startAction.invoke()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//        motionLayout.progress = positionOffset * 1.65f/* / (numPages - 1)*/
        val pageIndex = if (positionOffset > 0.5) Math.min(position + 1,
                pages.size - 1) else position
//        activityUI.viewBinding.setVariable(BR.page, pages[pageIndex])
        val alpha = if (positionOffset > 0.5) 1.0f - positionOffset * 1.5f else 1.0f
        activityUI.viewBinding.setVariable(BR.progress, Math.max(0.0f, alpha))
        activityUI.viewBinding.executePendingBindings()
        Log.d("onPageScrolled", position, positionOffset, positionOffsetPixels,
                motionLayout.progress)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageSelected(position: Int) {
        //Execute transitions when the page changes
        Log.d("onPageSelected", position)
        motionLayout.transitionToStart()
        motionLayout.transitionToEnd()
        //Set the page data binding variable to display the selected page icon, title, details.
        when {
            position == (pages.size - 1) -> {
                startButton.visibility = View.VISIBLE
                button_content.transitionToStart()
                button_content.transitionToEnd()
            }
            else -> {
                button_content.transitionToStart()
                startButton.visibility = View.INVISIBLE
            }
        }
        activityUI.viewBinding.setVariable(BR.page, pages[position])
        activityUI.viewBinding.executePendingBindings()
    }
}

class TutorialActivityUI : AnkoComponent<TutorialActivity> {
    lateinit var viewBinding: ViewDataBinding

    override fun createView(ui: AnkoContext<TutorialActivity>) = with(ui) {
        inflatedDataBindingAnkoView(R.layout.activity_tutorial, {
            id = R.id.tut_content
            viewBinding.setVariable(BR.page, ui.owner.firstPage)
            viewBinding.setVariable(BR.progress, 1.0f)
            this@TutorialActivityUI.viewBinding = this.viewBinding
        })
    }
}

@SuppressLint("ViewConstructor")
class TutorialPageUI(val page: Page, ctx: Context) : View(ctx), AnkoComponent<View>,
        AnkoViewProvider {
    override val view: View by lazy {
        createView(AnkoContext.create(ctx, this as View))
    }

    override fun createView(ui: AnkoContext<View>): View = with(ui) {
        inflatedDataBindingAnkoView(R.layout.widget_tutorial_page, {
            id = page.id
            viewBinding.setVariable(BR.page, page)
        })
    }
}

class TutorialAdapter(var viewList: MutableList<View> = mutableListOf()) : PagerAdapter() {
    var data: List<View>
        get() {
            return viewList
        }
        set(viewList) {
            if (viewList.isEmpty()) {
                this.viewList.clear()
            } else {
                this.viewList.addAll(viewList)
            }
            notifyDataSetChanged()
        }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val view = viewList[position]
        collection.addView(view)
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return viewList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }
}
