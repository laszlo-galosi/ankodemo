package android.support.constraint.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;

/**
 * ImageFilterView sub class to set layer drawable with altSrc property.
 *
 * @author László Gálosi
 * @see ImageFilterView#ImageFilterView(Context, AttributeSet, int)
 * @see ImageFilterView#setCrossfade(float)
 * @since 27/08/18
 */
public class DataBindingImageFilterView extends ImageFilterView {

    public DataBindingImageFilterView(Context context) {
        this(context, null);
    }

    public DataBindingImageFilterView(Context context,
            AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataBindingImageFilterView(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayers(Drawable source, Drawable altSrc) {
        this.mLayers = new Drawable[] { source, altSrc };
        this.mLayer = new LayerDrawable(this.mLayers);
        this.mLayer.getDrawable(1).setAlpha((int) (255.0F * getCrossfade()));
        setImageDrawable(mLayer);
    }

    @Override public void setCrossfade(float crossfade) {
        super.setCrossfade(crossfade);
        if (this.mLayers != null) {
            float xfade = 255.0F * crossfade;
            this.mLayer.getDrawable(0).setAlpha((int) (255 - xfade));
            this.mLayer.getDrawable(1).setAlpha((int) xfade);
            super.setImageDrawable(this.mLayer);
        }
    }

    @BindingAdapter("altSrc")
    public static void bindAltDrawable(DataBindingImageFilterView view, Drawable drawable) {
        view.setLayers(view.getDrawable(), drawable);
    }
}
