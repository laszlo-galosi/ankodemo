package org.example.ankodemo.util;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by László Gálosi on 19/09/17
 */
public class DataBindingHelpers {
    @BindingAdapter("srcCompat")
    public static void bindSrcCompat(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    @BindingAdapter("android:src")
    public static void bindImageUri(ImageView view, Uri imageUri) {
        view.setImageURI(imageUri);
    }

    @BindingAdapter("android:src")
    public static void bindImageDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("android:src")
    public static void bindImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("passwordToggleEnabled")
    public static void bindPasswordToggleEnabled(TextInputLayout view, boolean enable) {
        view.setPasswordVisibilityToggleEnabled(enable);
    }

    @BindingAdapter("fieldId")
    public static void bindFieldId(View view, int fieldId) {
        view.setId(fieldId);
    }
}
