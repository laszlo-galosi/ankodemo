package org.example.ankodemo

import android.content.Context
import android.support.constraint.motion.MotionLayout
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import trikita.log.Log

/**
 * MotionLayout sub class with ViewPager.OnPageChangeListener to set the transition
 * progress on onPageScrolled to the positionOffset values so the transition is followed by the
 * user scrolling.
 * app:motionSpeedFactor - the transition speed multiplier to modify the transition progress.
 * The positionOffset is multiplied with this float number.
 * @author László Gálosi
 * @since 21/08/18
 */
class ViewPagerMotionLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : MotionLayout(context, attrs, defStyleAttr),
        ViewPager.OnPageChangeListener {

    var motionSpeedFactor: Float = 1.0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerMotionLayout)
        motionSpeedFactor = a.getFloat(R.styleable.ViewPagerMotionLayout_motionSpeedFactor, 1.0f)
        a.recycle()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//        Set the progress regarding the view pager number of pages, if you want the transition
//        follow the the current selected page
//        val numPages = 3
//        progress = positionOffset / (numPages - 1)
//        Set the progress of the transition multiplied with a factor to faster transition as the user
//       scrolled .
        progress = positionOffset * motionSpeedFactor
        Log.d("onPageScrolled", position, positionOffset, positionOffsetPixels, progress)
    }

    override fun onPageSelected(position: Int) {
    }

}
