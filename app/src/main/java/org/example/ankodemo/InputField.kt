package org.example.ankodemo

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.editText
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * Created by László Gálosi on 04/12/17
 */
open class InputField : LinearLayout {

    var label: String? = null
    var hintText: String? = null
    var errorText: String? = null
    @ColorInt
    var helperTextColor: Int = Color.LTGRAY
    var value: String
        get() = editField.text.toString()
        set(text) {
            value = text
            editField.setText(text)
        }
    var labelEnabled: Boolean = true
    var errorEnabled: Boolean = true
    var inputType: Int = InputType.TYPE_CLASS_TEXT
    var imeAction: Int = EditorInfo.IME_ACTION_NONE
    var imeLabel: String? = null
    var onEditorAction: TextView.OnEditorActionListener? = null

    private lateinit var labelView: TextView
    private lateinit var errorView: TextView
    private lateinit var editField: EditText

    private fun init() = AnkoContext.createDelegate(this).apply {
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        orientation = VERTICAL

        labelView = textView(label) {
            id = R.id.label
            textSize = 16f
        }

        editField = editText {
            id = R.id.editField
            textSize = 16f
        }

        errorView = textView(errorText) {
            textSize = 12f
        }
    }

    fun bind() {
        with(labelView) {
            visibility = if (labelEnabled) View.VISIBLE else View.GONE
            text = label
        }

        with(editField) {
            setText(value)
            hint = hintText
            setImeActionLabel(imeLabel, imeAction)
            setOnEditorActionListener(onEditorAction)
        }

        with(errorView) {
            visibility = if (errorEnabled) View.VISIBLE else View.GONE
            text = errorText
            textColor = helperTextColor
        }
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr) {
        init()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.customInputField(init: InputField.() -> Unit) = customInputField(init, 0)

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.customInputField(init: InputField.() -> Unit, theme: Int = 0) = ankoView(
        { InputField(it) }, theme, { init(); bind() })
