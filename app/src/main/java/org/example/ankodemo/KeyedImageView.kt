package org.example.ankodemo

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import org.jetbrains.anko.collections.forEach

/**
 * @author László Gálosi
 * @since 21/08/18
 */
class KeyedImageView : AppCompatImageView, HasKeyValue<Int, Drawable> {
    var keyMap: SparseArray<Any> = SparseArray()

    var valueMap: SparseArray<Any> = SparseArray()

    val defaultDrawable: Drawable? by lazy {
        ContextCompat.getDrawable(this.context,
                android.R.drawable.screen_background_dark_transparent)
    }

    override fun keyMap(): SparseArray<Int> {
        val values = SparseArray<Int>()
        valueMap.forEach { key, value -> values.put(key, value as Int) }
        return values
    }

    override fun valueMap(): SparseArray<Drawable> {
        val values = SparseArray<Drawable>()
        valueMap.forEach { key, value -> values.put(key, value as Drawable) }
        return values
    }

    override fun valueByKey(key: Int): Drawable = valueMap.get(key, defaultDrawable) as Drawable

    override fun indexByKey(key: Int): Int = valueMap.indexOfKey(key)

    override fun indexByValue(value: Drawable): Int = valueMap.indexOfValue(value)

    override fun keyAt(index: Int): Int = keyMap.keyAt(index)

    constructor(context: Context) : super(context) {}

    var currentKey: Int = 0
        set(key) {
//                Log.d("setCurrentKey", key, valueByKey(key))
            setImageDrawable(valueByKey(key))
        }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr) {
        initAttrs(context, attrs)
    }

    override fun getDrawable(): Drawable? = if (valueMap != null) valueByKey(currentKey) else null

    internal fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val attributeArray = context.obtainStyledAttributes(attrs, R.styleable.KeyedImageView)
            initKeysAndValues(attributeArray)
            var drawableTop: Drawable? = null
            currentKey = attributeArray.getInt(R.styleable.KeyedImageView_currentKey, 0)
            attributeArray.recycle()
        }
    }

    protected fun initKeysAndValues(attr: TypedArray) {
        val choiceKeysRes = attr.getResourceId(R.styleable.KeyedImageView_drawableKeys, 0)
        val choiceValuesRes = attr.getResourceId(R.styleable.KeyedImageView_drawableValues, 0)
        if (choiceKeysRes <= 0) {
            return
        }
        if (!isInEditMode) {
            val choiceKeys = context.resources.obtainTypedArray(choiceKeysRes)
            val choiceValues = context.resources.obtainTypedArray(choiceValuesRes)
            keyMap = createSparseArray(choiceKeys, TypedValue.TYPE_INT_DEC) { i -> i }
            valueMap = createSparseArray(choiceValues, TypedValue.TYPE_REFERENCE) { it -> it }
        } else {
            val choiceIds = context.resources.getIntArray(choiceKeysRes)
            keyMap = createSparseArrayInEditMode(choiceIds.toList()) { it -> it }
            val choiceIdValues = context.resources.getIntArray(choiceValuesRes)
            valueMap = createSparseArrayInEditMode(choiceIdValues.toList()) { it -> it }
        }
    }

    private fun createSparseArray(typedArray: TypedArray?,
            typedValueType: Int, keyAt: (kotlin.Int) -> Int): SparseArray<Any> {
        val len = typedArray?.length() ?: 0
        val array = SparseArray<Any>(len)
        for (i in 0 until len) {
            val resId = typedArray!!.getResourceId(i, -1)
            val key = keyAt.invoke(i)
            when (typedValueType) {
                TypedValue.TYPE_REFERENCE ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        array.put(key, typedArray.getDrawable(i))
                    } else {
                        val drawableId = typedArray.getResourceId(0, -1)
                        if (drawableId != -1) array.put(key,
                                ContextCompat.getDrawable(this.context, drawableId))
                    }
                TypedValue.TYPE_FLOAT -> array.put(key,
                        typedArray.getFloat(i, 0.0f))
                TypedValue.TYPE_INT_BOOLEAN -> array.put(key,
                        typedArray.getBoolean(i, java.lang.Boolean.FALSE))
                else -> array.put(key, typedArray.getInt(i, 0))
            }
        }
        return array
    }

    private fun <T : Any> createSparseArrayInEditMode(typedList: List<T>,
            keyAt: (kotlin.Int) -> Int): SparseArray<Any> {
        val len = typedList?.size ?: 0
        val array = SparseArray<Any>(len)
        for (i in 0 until len) {
            array.put(keyAt.invoke(i), typedList[i])
        }
        return array
    }
}
